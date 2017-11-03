package doext.define;

import core.object.DoUIModule;
import core.object.DoProperty;
import core.object.DoProperty.PropertyDataType;


public abstract class do_RichLabel1_MAbstract extends DoUIModule{

	protected do_RichLabel1_MAbstract() throws Exception {
		super();
	}
	
	/**
	 * 初始化
	 */
	@Override
	public void onInit() throws Exception{
        super.onInit();
        //注册属性
		this.registProperty(new DoProperty("fontColor", PropertyDataType.String, "000000", false));
		this.registProperty(new DoProperty("fontSize", PropertyDataType.Number, "9", false));
		this.registProperty(new DoProperty("textFlag", PropertyDataType.String, "normal", true));
		this.registProperty(new DoProperty("fontStyle", PropertyDataType.String, "normal", false));
		this.registProperty(new DoProperty("maxHeight", PropertyDataType.Number, "100", true));
		this.registProperty(new DoProperty("maxLines", PropertyDataType.Number, "1", true));
		this.registProperty(new DoProperty("maxWidth", PropertyDataType.Number, "100", true));
		this.registProperty(new DoProperty("text", PropertyDataType.String, "", false));
		this.registProperty(new DoProperty("textAlign", PropertyDataType.String, "left", true));
		this.registProperty(new DoProperty("span", PropertyDataType.String, "", false));
		this.registProperty(new DoProperty("linesSpace", PropertyDataType.Number, "", false));
	}
}