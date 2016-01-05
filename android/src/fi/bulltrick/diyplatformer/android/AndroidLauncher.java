package fi.bulltrick.diyplatformer.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import fi.bulltrick.diyplatformer.DIYPlatformer;
import fi.bulltrick.diyplatformer.Platform;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;

		PlatformAndroid platform = new PlatformAndroid();
		platform.setActivity(this);

		initialize(new DIYPlatformer(platform), config);
	}
}
