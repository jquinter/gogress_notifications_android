package cl.iluminadoschile.pako.floatingdiv;

/*
Copyright 2011 jawsware international

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;

public class CustomOverlayView extends OverlayView {

	private TextView info;
    public boolean is_visible;
	int initialX;
    int initialY;

	public CustomOverlayView(OverlayService service) {
        super(service, R.layout.overlay, 1);
        is_visible = false;
	}

    public int getLayoutGravity() {
        return Gravity.TOP + Gravity.CENTER_HORIZONTAL;
    }

	public int getGravity() {
		return Gravity.TOP + Gravity.RIGHT;
	}
	
	@Override
	protected void onInflateView() {
		info = (TextView) this.findViewById(R.id.textview_info);
	}

	@Override
	protected void refreshViews() {
		info.setText("STARTED...\nWAITING");
	}

	@Override
	protected void onTouchEvent_Up(MotionEvent event) {
		info.setText("UP\nPOINTERS: " + event.getPointerCount());
	}

	@Override
	protected void onTouchEvent_Move(MotionEvent event) {
		info.setText("MOVE\nPOINTERS: " + event.getPointerCount());

        /*
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        LayoutParams lp = (LayoutParams) info.getLayoutParams();

        int left = lp.leftMargin + (currentX - initialX);
        int top = lp.topMargin + (currentY - initialY);
        int right = lp.rightMargin - (currentX - initialX);
        int bottom = lp.bottomMargin - (currentY - initialY);

        lp.rightMargin = right;
        lp.leftMargin = left;
        lp.bottomMargin = bottom;
        lp.topMargin = top;

        info.setLayoutParams(lp);
        */
	}

	@Override
	protected void onTouchEvent_Press(MotionEvent event) {
		info.setText("DOWN\nPOINTERS: " + event.getPointerCount());

        initialX = (int) event.getRawX();
        initialY = (int) event.getRawY();
	}

	@Override
	public boolean onTouchEvent_LongPress() {
		info.setText("LONG\nPRESS");

        Context context = getContext();
        Intent settingsIntent = new Intent(Intent.ACTION_MAIN);
        settingsIntent.setClassName(Constants.ACTION.prefix, Constants.ACTION.prefix + ".SettingsActivity");
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingsIntent);

        return true;
	}

    @Override
    public boolean isVisible(){
        return is_visible;
    }

    public void setVisible(){
        is_visible = true;
        this.refresh();
    }

    public void setInvisible(){
        is_visible = false;
        this.refresh();
    }

    public void setText(String text){
        info.setText(text);
    }
	
	
}
