package doext.ui;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * If an object of this type is attached to the text of a TextView
 * with a movement method of LinkMovementMethod, the affected spans of
 * text can be selected.  If clicked, the {@link #onClick} method will
 * be called.
 */
public class DoNoLineCllikcSpan extends ClickableSpan {
	
	protected CharSequence mCharSequence;
	protected int mLinkColor;
	
    public DoNoLineCllikcSpan(CharSequence charSequence, int linkColor) {
        super();
        this.mCharSequence = charSequence;
        this.mLinkColor = linkColor;
    }
    
    /**
     * Remove the text underlined and in the link color.
     */
    @Override
    public void updateDrawState(TextPaint ds) {
    	if(mLinkColor == 0) {
    		ds.setColor(ds.linkColor);
    	} else {
    		ds.setColor(mLinkColor);
    	}
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
    	
    }
}