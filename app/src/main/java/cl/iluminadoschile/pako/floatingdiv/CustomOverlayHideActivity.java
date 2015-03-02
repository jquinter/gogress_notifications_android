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

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

public class CustomOverlayHideActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

/*
        Intent startIntent = new Intent(this, CustomOverlayService.class);
        startIntent.setAction(Constants.ACTION.SETTINGS_ACTION);
        startService(startIntent);
*/
//		CustomOverlayService.stop();

        Intent settingsIntent = new Intent(Intent.ACTION_MAIN);
        settingsIntent.setClassName(Constants.ACTION.prefix, Constants.ACTION.prefix + ".SettingsActivity");
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingsIntent);

        finish();
		
	}
    
}
