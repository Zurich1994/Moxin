package com.heu.moxin.dialog;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.heu.moxin.R;
import com.heu.moxin.activity.BaseActivity;

public class ExitGroupDialog extends BaseActivity{
    private TextView text;
    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_actionsheet);
        
        text = (TextView) findViewById(R.id.tv_text);
        exitBtn = (Button) findViewById(R.id.btn_exit);
        
        text.setText(R.string.exit_group_hint);
        String toast = getIntent().getStringExtra("deleteToast");
        if(toast != null)
        	text.setText(toast);
        exitBtn.setText(R.string.exit_group);
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
