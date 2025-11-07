package androidx.core.app;

import android.util.Log;

import com.tb.pdfly.BuildConfig;

public abstract class SpecialJobService extends BaseJobIntentService {

    protected void mToast(String str) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, str);
        }
    }
}
