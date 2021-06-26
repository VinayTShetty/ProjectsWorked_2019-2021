package com.proofofLife;

import android.app.Activity;

import com.kaopiz.kprogresshud.KProgressHUD;

public class UIHelper {
    private KProgressHUD hud;
    Activity activity;
    public UIHelper(Activity activity_loc){
        this.activity=activity_loc;
        intializeView();
    }
    private void intializeView(){
        hud = KProgressHUD.create(activity);
    }

    public  void show_Progress_dialog(String title,String subtitle){
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(subtitle)
                .setCancellable(false)
                .show();
    }
    public void hide_progressDialog(){
            if(hud!=null){
                if(hud.isShowing()){
                    hud.dismiss();
                }
            }
    }
}
