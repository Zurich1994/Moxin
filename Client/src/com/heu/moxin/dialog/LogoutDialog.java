package com.heu.moxin.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.heu.moxin.R;
import com.heu.moxin.activity.LoginActivity;

public class LogoutDialog extends Activity {

	Button button_exit;   
    Button button_cancel;
    private TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logout_actionsheet);
		text = (TextView) findViewById(R.id.tv_text);
		button_exit = (Button)findViewById(R.id.btn_exit);   
	    button_cancel = (Button)findViewById(R.id.btn_cancel);
	    text.setText(R.string.logout_hint);
        button_exit.setText(R.string.logout);
	    button_exit.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	EMChatManager.getInstance().logout();
            	startActivity(new Intent(LogoutDialog.this, LoginActivity.class));
            	logout(v);
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cancel(v);
             //   dialog.dismiss();
            }
        });
	}
	
	
	  public void logout(View view){
	    	setResult(RESULT_OK);
	        finish();
	        
	    }
	    
	    public void cancel(View view) {
	        finish();
	    }
	    
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        finish();
	        return true;
	    }


}
