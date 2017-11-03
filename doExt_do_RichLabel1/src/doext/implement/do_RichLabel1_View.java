package doext.implement;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import core.DoServiceContainer;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoIUIModuleView;
import core.object.DoInvokeResult;
import core.object.DoUIModule;
import doext.define.do_RichLabel1_IMethod;
import doext.define.do_RichLabel1_MAbstract;
import doext.ui.DoNoLineCllikcSpan;

/**
 * 自定义扩展UIView组件实现类，此类必须继承相应VIEW类，并实现DoIUIModuleView,Do_Label_IMethod接口；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象；
 * 获取DoInvokeResult对象方式new DoInvokeResult(this.model.getUniqueKey());
 */
public class do_RichLabel1_View extends TextView implements DoIUIModuleView,do_RichLabel1_IMethod{
	
	/**
	 * 每个UIview都会引用一个具体的model实例；
	 */
	private do_RichLabel1_MAbstract model;
	

	public do_RichLabel1_View(Context context) {
		super(context);
	}
	
	
	
	/**
	 * 初始化加载view准备,_doUIModule是对应当前UIView的model实例
	 */
	@Override
	public void loadView(DoUIModule _doUIModule) throws Exception {
		this.model = (do_RichLabel1_MAbstract)_doUIModule;
		this.setTextSize(TypedValue.COMPLEX_UNIT_PX, DoUIModuleHelper.getDeviceFontSize(_doUIModule, "17"));
		this.setTextColor(Color.BLACK);
		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		this.setMaxLines(1);
		this.setEllipsize(TruncateAt.END);
	}
	
	/**
	 * 动态修改属性值时会被调用，方法返回值为true表示赋值有效，并执行onPropertiesChanged，否则不进行赋值；
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public boolean onPropertiesChanging(Map<String, String> _changedValues) {
		return true;
	}
	
	/**
	 * 属性赋值成功后被调用，可以根据组件定义相关属性值修改UIView可视化操作；
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public void onPropertiesChanged(Map<String, String> _changedValues) {
		DoUIModuleHelper.handleBasicViewProperChanged(this.model, _changedValues);
		DoUIModuleHelper.setFontProperty(this.model, _changedValues);

		if (_changedValues.containsKey("textAlign")) {
			String _textAlign = _changedValues.get("textAlign");
			if (_textAlign.equals("center")) {
				this.setGravity(Gravity.CENTER);
			} else if (_textAlign.equals("right")) {
				this.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			} else {
				this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}
		}
		if (_changedValues.containsKey("maxWidth")) {
			int _maxWidth = (int) (DoTextHelper.strToDouble(_changedValues.get("maxWidth"), 100) * this.model.getXZoom());
			this.setMaxWidth(_maxWidth);
		}
		if (_changedValues.containsKey("maxHeight")) {
			int _maxHeight = (int) (DoTextHelper.strToDouble(_changedValues.get("maxHeight"), 100) * this.model.getYZoom());
			this.setMaxHeight(_maxHeight);
		}
		if (_changedValues.containsKey("maxLines")) {
			int _maxLines = DoTextHelper.strToInt(_changedValues.get("maxLines"), 1);
			if(_maxLines <= 0){
				this.setMaxLines(Integer.MAX_VALUE);
			}else{
				this.setMaxLines(_maxLines);
			}
		}
		
		// 设置行间距
		if (_changedValues.containsKey("linesSpace")) {
			float _linesSpace = DoTextHelper.strToInt(_changedValues.get("linesSpace"), 0);
			if (_linesSpace >= 0) {
				this.setLineSpacing(_linesSpace, 1f);// 第一个参数是精确值，后一个是倍数。
			}
		}
		
		if (_changedValues.containsKey("span") || _changedValues.containsKey("text")) {
			String text = getText().toString();
			String span = _changedValues.get("span");
			if(span == null) {
				try {
					span = model.getPropertyValue("span");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(text.length() > 0 && !TextUtils.isEmpty(span)) {
				SpannableString spannable = new SpannableString(text);
				try {
					JSONArray jsonArray = new JSONArray(span);
					int len = jsonArray.length();
					if(len == 0) {
						return;
					}
					for (int i = 0; i < len; i++) {
						JSONObject jsonObject = (JSONObject)jsonArray.get(i);
						if(jsonObject.has("strMatch")){
							String strMatch = jsonObject.getString("strMatch");
							Pattern p = Pattern.compile(strMatch);    
					        Matcher m = p.matcher(text);
					        while (m.find()) {
					        	setSpan(jsonObject, spannable, m.start(), m.end());
					        }
						}
						if(jsonObject.has("substring")){
							String substring = jsonObject.getString("substring");
							String[] indexArray = substring.split(",");
							int start = 0;
							int end = 0;
							if (indexArray.length == 2) {
								start = Integer.parseInt(indexArray[0]);
								end = Integer.parseInt(indexArray[1]);
							}
					        setSpan(jsonObject, spannable, start, end);
						}
					}
					setText(spannable);
				} catch (Exception e) {
					DoServiceContainer.getLogEngine().writeError("span 属性格式设置不正确！", e);
				}
			}
		}
	}
	
	private void setSpan(final JSONObject obj, SpannableString spannable, int start, int end) throws JSONException{
		ForegroundColorSpan foregroundColorSpan = getForegroundColorSpan(obj);
		if (foregroundColorSpan != null) {
			spannable.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		StyleSpan styleSpan = getStyleSpan(obj);
		if (styleSpan != null) {
			spannable.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if(obj.has("allowTouch")){
			String allowTouch = obj.getString("allowTouch");
			if("true".equals(allowTouch)) {
				setMovementMethod(LinkMovementMethod.getInstance());
				CharSequence charSequence = spannable.subSequence(start, end);
				spannable.setSpan(new DoNoLineCllikcSpan(charSequence,foregroundColorSpan.getForegroundColor()) {
					
					@Override
					public void onClick(View widget) {
						DoInvokeResult jsonResult = new DoInvokeResult(model.getUniqueKey());
						JSONObject node = new JSONObject();
						try {
							node.put("content", mCharSequence);
							node.put("tag", obj.has("tag") ? obj.get("tag") : "");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						jsonResult.setResultNode(node);
						model.getEventCenter().fireEvent("touch", jsonResult);
					}
				}, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
	
	private ForegroundColorSpan getForegroundColorSpan(JSONObject obj) throws JSONException{
		if(obj.has("spanStyle")){
			String fontColor = "";
			String spanStyle = obj.getString("spanStyle");
			JSONObject jsonObj = new JSONObject(spanStyle);
			if(jsonObj.has("fontColor")){
				fontColor = jsonObj.getString("fontColor");
				return new ForegroundColorSpan(DoUIModuleHelper.getColorFromString(fontColor, Color.TRANSPARENT));
			}
		}
		return null;
	}
	
	private StyleSpan getStyleSpan(JSONObject obj) throws JSONException{
		if (obj.has("spanStyle")) {
			String fontStyle = "";
			String spanStyle = obj.getString("spanStyle");
			JSONObject jsonObj = new JSONObject(spanStyle);
			StyleSpan styleSpan = null;
			if (jsonObj.has("fontStyle")) {
				fontStyle = jsonObj.getString("fontStyle");
				if ("bold".equals(fontStyle)) {
					styleSpan = new StyleSpan(Typeface.BOLD);
				} else if ("italic".equals(fontStyle)) {
					styleSpan = new StyleSpan(Typeface.ITALIC);
				} else if ("bold_italic".equals(fontStyle)) {
					styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
				} else if ("normal".equals(fontStyle)) {
					styleSpan = new StyleSpan(Typeface.NORMAL);
				}
				return styleSpan;
			}
		}
		return null;
	}
	
	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas,
			DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult)throws Exception {
		//...do something
		return false;
	}
	
	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用，
	 * 可以根据_methodName调用相应的接口实现方法；
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名
	 * #如何执行异步方法回调？可以通过如下方法：
	 *	_scriptEngine.callback(_callbackFuncName, _invokeResult);
	 * 参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	   获取DoInvokeResult对象方式new DoInvokeResult(this.model.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas,
			DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		//...do something
		return false;
	}
	
	/**
	* 释放资源处理，前端JS脚本调用closePage或执行removeui时会被调用；
	*/
	@Override
	public void onDispose() {
		//...do something
	}
	
	/**
	* 重绘组件，构造组件时由系统框架自动调用；
	  或者由前端JS脚本调用组件onRedraw方法时被调用（注：通常是需要动态改变组件（X、Y、Width、Height）属性时手动调用）
	*/
	@Override
	public void onRedraw() {
		this.setLayoutParams(DoUIModuleHelper.getLayoutParams(this.model));
	}
	
	/**
	 * 获取当前model实例
	 */
	@Override
	public DoUIModule getModel() {
		return model;
	}
}
