package cn.xcnya.bantracker;

import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;
import cn.xcnya.bantracker.modules.Tracker;

// Required @ExtensionInfo annotation
@ExtensionInfo(name = "Ban Tracker",author = "libxcnya.so, SakuraNiroku, renshengongji",version = "1.7")
public class BanTracker extends Extension {
    public static OpenAPI openAPI;

    @Override
    public void initialize(OpenAPI openAPI) {
        BanTracker.openAPI = openAPI;
        openAPI.registerFeature(new Tracker());
    }

    @Override
    public void onUnload() {
        Tracker.INSTANCE.disableTimer();
    }
}