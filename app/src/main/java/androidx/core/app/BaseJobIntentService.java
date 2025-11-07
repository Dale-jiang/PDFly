package androidx.core.app;

import android.util.Log;

@Deprecated
public abstract class BaseJobIntentService extends JobIntentService {

    protected String tag = "BaseJobIntentService";

    @Override
    GenericWorkItem dequeueWork() {
        try {
            Log.w(tag, "BaseJobIntentService dequeueWork");
            return super.dequeueWork();
        } catch (Exception i) {
            return null;
        }
    }

}