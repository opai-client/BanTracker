package cn.xcnya.bantracker;

import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;
import cn.xcnya.bantracker.modules.Tracker;

// Required @ExtensionInfo annotation
@ExtensionInfo(name = "Ban Tracker",author = "libxcnya.so, SakuraNiroku, renshengongji",version = BanTracker.versionID)
public class BanTracker extends Extension { //多人运动插件↑

    public static final String versionID = "2.0.2";

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