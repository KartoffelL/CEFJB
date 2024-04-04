#include "Kartoffel_Licht_CEFFunk.h"

#include <jni.h>

#include <iostream>
#include <vector>
#include <string>

#include "include/cef_app.h"
#include "include/cef_browser.h"
#include "include/cef_command_line.h"
#include "include/cef_client.h"
#include "include/cef_render_handler.h"
#include <include/views/cef_view.h>

class JNICallback {
public:
    JNIEnv* env;
    jobject object;

    jmethodID cb_paint;
    jmethodID cb_windowSize;
    jmethodID cb_audioStart;
    jmethodID cb_audioPacket;
    jmethodID cb_audioStop;
    jmethodID cb_audioError;
    jmethodID cb_audioParams;
    jmethodID cb_cursor;
    jmethodID cb_loadingpr;
    jmethodID cb_tooltp;
    jmethodID cb_fullscrn;
    jmethodID cb_favc;
    jmethodID cb_title;
    jmethodID cb_addrs;
};
class BrowserRegister {
public:
    std::vector<CefRefPtr<CefBrowser>> b;

    int add(CefRefPtr<CefBrowser> browser) {
        b.push_back(browser);
        return browser.get()->GetIdentifier();
    }

    int getID(CefRefPtr<CefBrowser> browser) {
        return browser.get()->GetIdentifier();
    }

    CefRefPtr<CefBrowser> getBrowser(int id) {
        for (int i = 0; i < b.size(); i++)
            if (b[i].get()->GetIdentifier() == id)
                return b[i];
        return nullptr;
        std::cerr << "Browser not found by ID " + std::to_string(id) + "!" << std::endl;
    }
};

class SimpleClient : public CefClient, public CefDisplayHandler, public CefLifeSpanHandler, public CefLoadHandler, public CefRenderHandler, public CefAudioHandler {
public:
    JNICallback *call;
    BrowserRegister *reg;
    explicit SimpleClient(JNICallback* c, BrowserRegister *r) {
        this->call = c;
        this->reg = r;
    }

    virtual CefRefPtr<CefRenderHandler> GetRenderHandler() override {
        return this;
    }
    virtual CefRefPtr<CefDisplayHandler> GetDisplayHandler() override {
        return this;
    }
    virtual CefRefPtr<CefLifeSpanHandler> GetLifeSpanHandler() override {
        return this;
    }
    virtual CefRefPtr<CefLoadHandler> GetLoadHandler() override {
        return this;
    }

    virtual void OnAfterCreated(CefRefPtr<CefBrowser> browser) override {

    }

    virtual void GetViewRect(CefRefPtr<CefBrowser> browser, CefRect& rect) override {
        jintArray array = (jintArray)call->env->CallObjectMethod(call->object, call->cb_windowSize, reg->getID(browser));
        jint* data = call->env->GetIntArrayElements(array, 0);
        rect.x = 0;
        rect.y = 0;
        rect.width = data[0];
        rect.height = data[1];
        call->env->ReleaseIntArrayElements(array, data, 0);
    }

    virtual void OnAddressChange(CefRefPtr<CefBrowser> browser, CefRefPtr<CefFrame> frame, const CefString& url) override {
        call->env->CallVoidMethod(call->object, call->cb_addrs, reg->getID(browser), call->env->NewStringUTF(url.ToString().c_str()));
    }

    virtual void OnTitleChange(CefRefPtr<CefBrowser> browser, const CefString& title) override {
        call->env->CallVoidMethod(call->object, call->cb_title, reg->getID(browser), call->env->NewStringUTF(title.ToString().c_str()));
    }

    virtual void OnFaviconURLChange(CefRefPtr<CefBrowser> browser, const std::vector<CefString>& icon_urls) override {
        jobjectArray arr = call->env->NewObjectArray(icon_urls.size(), call->env->FindClass("java/lang/String"), call->env->NewStringUTF(""));
        for (int i = 0; i < icon_urls.size(); i++) call->env->SetObjectArrayElement(arr, i, call->env->NewStringUTF(icon_urls[i].ToString().c_str()));
        call->env->CallVoidMethod(call->object, call->cb_favc, reg->getID(browser), arr);
    }

    virtual void OnFullscreenModeChange(CefRefPtr<CefBrowser> browser, bool fullscreen) override {
        call->env->CallVoidMethod(call->object, call->cb_fullscrn, reg->getID(browser), fullscreen);
    }

    virtual bool OnTooltip(CefRefPtr<CefBrowser> browser, CefString& text) override {
        call->env->CallVoidMethod(call->object, call->cb_tooltp, reg->getID(browser), call->env->NewStringUTF(text.ToString().c_str()));
        return false;
    }

    virtual void OnStatusMessage(CefRefPtr<CefBrowser> browser, const CefString& value) override {}

    virtual bool OnConsoleMessage(CefRefPtr<CefBrowser> browser,
        cef_log_severity_t level,
        const CefString& message,
        const CefString& source,
        int line) override {
        return false;
    }

    virtual void OnLoadingProgressChange(CefRefPtr<CefBrowser> browser, double progress) override {
        call->env->CallVoidMethod(call->object, call->cb_loadingpr, reg->getID(browser), progress);
    }

    virtual bool OnCursorChange(CefRefPtr<CefBrowser> browser, CefCursorHandle cursor, cef_cursor_type_t type, const CefCursorInfo& custom_cursor_info) override{
        call->env->CallVoidMethod(call->object, call->cb_cursor, reg->getID(browser), (int)type);
        return false;
    }

    virtual void OnPaint(CefRefPtr<CefBrowser> browser, PaintElementType type, const RectList& dirtyRects, const void* buffer, int width, int height) override {
        call->env->CallVoidMethod(call->object, call->cb_paint, reg->getID(browser), buffer, width, height);
    }

    virtual bool GetAudioParameters(CefRefPtr< CefBrowser > browser, CefAudioParameters& params) {
        return false;
    }

    virtual void OnAudioStreamStarted(CefRefPtr< CefBrowser > browser, const CefAudioParameters& params, int channels) override {

    }
    virtual void OnAudioStreamStopped(CefRefPtr< CefBrowser > browser) override {

    }
    virtual void OnAudioStreamPacket(CefRefPtr< CefBrowser > browser, const float** data, int frames, INT64 pts) override {

    }
    virtual void OnAudioStreamError(CefRefPtr< CefBrowser > browser, const CefString& message) override {
        
    }

private:
    IMPLEMENT_REFCOUNTING(SimpleClient);
};

class SimpleApp : public CefApp, public CefRenderProcessHandler {
public:
    SimpleApp() {}

    virtual CefRefPtr<CefRenderProcessHandler> GetRenderProcessHandler() override {
        return this;
    }

    IMPLEMENT_REFCOUNTING(SimpleApp);
};

CefRefPtr<CefClient> client;
CefRefPtr<SimpleApp> app;
JNICallback callback;
BrowserRegister reg;

//Main entry function
JNIEXPORT jint JNICALL Java_Kartoffel_Licht_CEFFunk_init(JNIEnv* env, jclass clazz, jstring helperPath, jstring frameworkpath, jstring bundlepath, jstring rcachepath, jint bcolor, jint logsev) {
    CefMainArgs main_args(GetModuleHandle(nullptr));//Plattform-specific
    app = new SimpleApp;
    CefSettings settings;
    //Settings
    settings.no_sandbox = true;
    settings.background_color = bcolor;
    CefString(&settings.root_cache_path).FromString(rcachepath == 0 ? "" : env->GetStringUTFChars(rcachepath, 0));
    CefString(&settings.browser_subprocess_path).FromString(helperPath == 0 ? "" : env->GetStringUTFChars(helperPath, 0));
    CefString(&settings.framework_dir_path).FromString(frameworkpath == 0 ? "" : env->GetStringUTFChars(frameworkpath, 0));
    CefString(&settings.main_bundle_path).FromString(bundlepath == 0 ? "" : env->GetStringUTFChars(bundlepath, 0));
    switch (logsev) {
        case 0:
            settings.log_severity = LOGSEVERITY_DEFAULT;
        case 1:
            settings.log_severity = LOGSEVERITY_DEBUG;
        case 2:
            settings.log_severity = LOGSEVERITY_VERBOSE;
        case 3:
            settings.log_severity = LOGSEVERITY_INFO;
        case 4:
            settings.log_severity = LOGSEVERITY_WARNING;
        case 5:
            settings.log_severity = LOGSEVERITY_ERROR;
        case 6:
            settings.log_severity = LOGSEVERITY_FATAL;
        case 7:
            settings.log_severity = LOGSEVERITY_DISABLE;
    }
    CefInitialize(main_args, settings, app, nullptr);
    //----
    client = new SimpleClient(&callback, &reg);
    callback.env = env;
    callback.cb_paint = env->GetMethodID(clazz, "paint", "(IJII)V");
    callback.cb_audioStart = env->GetMethodID(clazz, "audioStart", "(IIIII)V");
    callback.cb_audioPacket = env->GetMethodID(clazz, "audioPacket", "(I[FIJ)V");
    callback.cb_audioStop = env->GetMethodID(clazz, "audioStop", "(I)V");
    callback.cb_audioError = env->GetMethodID(clazz, "audioError", "(ILjava/lang/String;)V");
    callback.cb_audioParams = env->GetMethodID(clazz, "getAudioParam", "()[I");
    callback.cb_windowSize = env->GetMethodID(clazz, "windowSize", "(I)[I");
    callback.cb_addrs = env->GetMethodID(clazz, "addressChanged", "(ILjava/lang/String;)V");
    callback.cb_cursor = env->GetMethodID(clazz, "cursorChange", "(II)V");
    callback.cb_favc = env->GetMethodID(clazz, "faviconChanged", "(I[Ljava/lang/String;)V");
    callback.cb_fullscrn = env->GetMethodID(clazz, "fullscreenModeChanged", "(IZ)V");
    callback.cb_loadingpr = env->GetMethodID(clazz, "loadingProcessChanged", "(ID)V");
    callback.cb_title = env->GetMethodID(clazz, "titleChanged", "(ILjava/lang/String;)V");
    callback.cb_tooltp = env->GetMethodID(clazz, "tooltip", "(ILjava/lang/String;)V");

    return 0;
}
JNIEXPORT jint JNICALL Java_Kartoffel_Licht_CEFFunk_createBrowser(JNIEnv* env, jclass, jstring url) {
    CefWindowInfo window_info;
    CefBrowserSettings browser_settings;
    window_info.SetAsWindowless(nullptr);
    //window_info.external_begin_frame_enabled = true; //For future me to worry about
    browser_settings.windowless_frame_rate = 60;
    CefRefPtr<CefBrowser> browser = CefBrowserHost::CreateBrowserSync(window_info, client, url == nullptr ? "about:credits" : env->GetStringUTFChars(url, 0), browser_settings, nullptr, nullptr);
    return reg.add(browser);
}
JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_deleteBrowser
(JNIEnv*, jclass, jint id, jboolean force) {
    reg.getBrowser(id).get()->GetHost().get()->CloseBrowser(force);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_sendMouseClickEvent (JNIEnv*, jclass, jint id, jint x, jint y, jint modifiers, jint type, jboolean mouseUp, jint clickcount) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    CefMouseEvent event;
    event.x = x;
    event.y = y;
    event.modifiers = modifiers;
    host.get()->SendMouseClickEvent(event, type == 0 ? CefBrowserHost::MouseButtonType::MBT_LEFT : type == 1 ? CefBrowserHost::MouseButtonType::MBT_RIGHT : CefBrowserHost::MouseButtonType::MBT_MIDDLE, mouseUp, clickcount);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_sendKeyEvent(JNIEnv*, jclass, jint id, jchar character, jint modifiers, jint nativeKeyCode, jint type) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    CefKeyEvent event;
    event.character = character;
    event.unmodified_character = character; //yeah, about that...
    event.windows_key_code = nativeKeyCode;
    event.native_key_code = nativeKeyCode;
    event.focus_on_editable_field = false;
    event.is_system_key = false;
    event.modifiers = modifiers;
    event.type = type == 0 ? cef_key_event_type_t::KEYEVENT_CHAR : type == 1 ? cef_key_event_type_t::KEYEVENT_KEYDOWN : type == 2 ? cef_key_event_type_t::KEYEVENT_KEYUP : cef_key_event_type_t::KEYEVENT_RAWKEYDOWN;
    host.get()->SendKeyEvent(event);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_sendMouseScrollEvent(JNIEnv*, jclass, jint id, jint x, jint y, jint modifiers, jint deltaX, jint deltaY) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    CefMouseEvent event;
    event.x = x;
    event.y = y;
    event.modifiers = modifiers;
    host.get()->SendMouseWheelEvent(event, deltaX, deltaY);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_sendMouseMoveEvent(JNIEnv*, jclass, jint id, jint x, jint y, jint modifiers, jboolean mouseLeave) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    CefMouseEvent event;
    event.x = x;
    event.y = y;
    event.modifiers = modifiers;
    host.get()->SendMouseMoveEvent(event, mouseLeave);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_sendCaptureLostEvent(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->SendCaptureLostEvent();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_sendExternalBeginFrame(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->SendExternalBeginFrame();
}

JNIEXPORT jboolean JNICALL Java_Kartoffel_Licht_CEFFunk_canGoBack(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    return browser.get()->CanGoBack();
}

JNIEXPORT jboolean JNICALL Java_Kartoffel_Licht_CEFFunk_canGoForward(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    return browser.get()->CanGoForward();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_goBack(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    browser.get()->GoBack();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_goForward(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    browser.get()->GoForward();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_stopLoading(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    browser.get()->StopLoad();
}

JNIEXPORT jboolean JNICALL Java_Kartoffel_Licht_CEFFunk_isLoading(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    return browser.get()->IsLoading();
}

JNIEXPORT jboolean JNICALL Java_Kartoffel_Licht_CEFFunk_hasDocument(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    return browser.get()->HasDocument();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_reload(JNIEnv*, jclass, jint id, jboolean ignoreCache) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    if (ignoreCache)
        browser.get()->ReloadIgnoreCache();
    else
        browser.get()->Reload();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_setFramerate(JNIEnv*, jclass, jint id, jint fps) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->SetWindowlessFrameRate(fps);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_loadURL(JNIEnv* env, jclass o, jint id, jstring url) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mainframe = browser.get()->GetMainFrame();
    mainframe.get()->LoadURL(env->GetStringUTFChars(url, 0));
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_viewSource(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->ViewSource();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_cut(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->Cut();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_copy(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->Copy();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_paste(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->Paste();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_selectall(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->SelectAll();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_delete(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->Delete();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_undo(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->Undo();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_redo(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefFrame> mf = browser.get()->GetMainFrame();
    mf.get()->Redo();
}

JNIEXPORT jboolean JNICALL Java_Kartoffel_Licht_CEFFunk_canZoom(JNIEnv*, jclass, jint id, jint dir) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    return host.get()->CanZoom(dir == -1 ? CEF_ZOOM_COMMAND_IN : dir == 0 ? CEF_ZOOM_COMMAND_RESET : CEF_ZOOM_COMMAND_OUT);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_zoom(JNIEnv*, jclass, jint id, jint dir) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->Zoom(dir == -1 ? CEF_ZOOM_COMMAND_IN : dir == 0 ? CEF_ZOOM_COMMAND_RESET : CEF_ZOOM_COMMAND_OUT);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_setZoom(JNIEnv*, jclass, jint id, jdouble lvl) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->SetZoomLevel(lvl);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_setFocus(JNIEnv*, jclass, jint id, jboolean focus) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->SetFocus(focus);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_wasHidden(JNIEnv*, jclass, jint id, jboolean hidden) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->WasHidden(hidden);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_wasResized(JNIEnv*, jclass, jint id) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->WasResized();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_find(JNIEnv* env, jclass, jint id, jstring tofind, jboolean forward, jboolean matchCase, jboolean findNext) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->Find(env->GetStringUTFChars(tofind, 0), forward, matchCase, findNext);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_stopFinding(JNIEnv*, jclass, jint id, jboolean clearSelection) {
    CefRefPtr<CefBrowser> browser = reg.getBrowser(id);
    CefRefPtr<CefBrowserHost> host = browser.get()->GetHost();
    host.get()->StopFinding(clearSelection);
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_doMessageLoopWork(JNIEnv* env, jobject t) {
    if(callback.object != nullptr)
        env->DeleteGlobalRef(callback.object);
    callback.object = env->NewGlobalRef(t);
    CefDoMessageLoopWork();
}

JNIEXPORT void JNICALL Java_Kartoffel_Licht_CEFFunk_free(JNIEnv* env, jclass t) {
    CefShutdown();
}

JNIEXPORT jobject JNICALL Java_Kartoffel_Licht_CEFFunk_createNew(JNIEnv* env, jclass, jlong ptr, jint capacity) {
    return env->NewDirectByteBuffer((void*)ptr, capacity);
}