package Kartoffel.Licht;

import java.awt.Cursor;
import java.nio.ByteBuffer;

/**
 * Interface to the shadow realm.<br><br>
 * The last invoker of doMessageLoopWork() will receive all callbacks.
 */
public class CEFFunk {
	
	/**
	 * Logseverities used for CEF.
	 */
	public static final int LOGSEVERITY_DEFAULT = 0,
							LOGSEVERITY_DEBUG = 1,
							LOGSEVERITY_VERBOSE = 2,
							LOGSEVERITY_INFO = 3,
							LOGSEVERITY_WARNING = 4,
							LOGSEVERITY_ERROR = 5,
							LOGSEVERITY_FATAL = 6,
							LOGSEVERITY_DISABLE = 7;
	
	/**
	 * Mousebuttons.
	 */
	public static final int MBT_LEFT = 0,
							MBT_RIGHT = 1,
							MBT_MIDDLE = 2;
	
	/**
	 * KEYEVENT_RAWKEYDOWN: Notification that a key transitioned from "up" to "down". <nr>
	 * KEYEVENT_KEYDOWN: Notification that a key was pressed. This does not necessarily correspond to a character depending on the key and language. Use KEYEVENT_CHAR for character input.<br>
	 * KEYEVENT_KEYUP: Notification that a key was released.
	 * KEYEVENT_CHAR Notification that a character was typed. Use this for text input. Key down events may generate 0, 1, or more than one character event depending on the key, locale, and operating system.
	 */
	public static final int KEYEVENT_CHAR = 0,
							KEYEVENT_KEYDOWN = 1,
							KEYEVENT_KEYUP = 2,
							KEYEVENT_RAWKEYDOWN = 3;
	
	public static final int CEF_ZOOM_COMMAND_IN = -1,
							CEF_ZOOM_COMMAND_RESET = 0,
							CEF_ZOOM_COMMAND_OUT = 1;
	
	///
	/// Cursor type values.
	///
	public static class CURSORS{
		  final public static int CT_POINTER = 0,
		  CT_CROSS = 1,
		  CT_HAND = 2,
		  CT_IBEAM = 3,
		  CT_WAIT = 4,
		  CT_HELP = 5,
		  CT_EASTRESIZE = 6,
		  CT_NORTHRESIZE = 7,
		  CT_NORTHEASTRESIZE = 8,
		  CT_NORTHWESTRESIZE = 9,
		  CT_SOUTHRESIZE = 10,
		  CT_SOUTHEASTRESIZE = 11,
		  CT_SOUTHWESTRESIZE = 12,
		  CT_WESTRESIZE = 13,
		  CT_NORTHSOUTHRESIZE = 14,
		  CT_EASTWESTRESIZE = 15,
		  CT_NORTHEASTSOUTHWESTRESIZE = 16,
		  CT_NORTHWESTSOUTHEASTRESIZE = 17,
		  CT_COLUMNRESIZE = 18,
		  CT_ROWRESIZE =19,
		  CT_MIDDLEPANNING = 20,
		  CT_EASTPANNING = 21,
		  CT_NORTHPANNING = 22,
		  CT_NORTHEASTPANNING = 23,
		  CT_NORTHWESTPANNING = 24,
		  CT_SOUTHPANNING = 25,
		  CT_SOUTHEASTPANNING = 26,
		  CT_SOUTHWESTPANNING = 27,
		  CT_WESTPANNING = 28,
		  CT_MOVE = 29,
		  CT_VERTICALTEXT = 30,
		  CT_CELL = 31,
		  CT_CONTEXTMENU = 32,
		  CT_ALIAS = 33,
		  CT_PROGRESS = 34,
		  CT_NODROP = 35,
		  CT_COPY = 36,
		  CT_NONE = 37,
		  CT_NOTALLOWED = 38,
		  CT_ZOOMIN = 39,
		  CT_ZOOMOUT = 40,
		  CT_GRAB = 41,
		  CT_GRABBING = 42,
		  CT_MIDDLE_PANNING_VERTICAL = 43,
		  CT_MIDDLE_PANNING_HORIZONTAL = 44,
		  CT_CUSTOM = 45,
		  CT_DND_NONE = 46,
		  CT_DND_MOVE = 47,
		  CT_DND_COPY = 48,
		  CT_DND_LINK = 49;
		
		public static int toSwingCursor(int cursor) {
			switch (cursor) {
			case CT_POINTER:
				return Cursor.DEFAULT_CURSOR;
			case CT_CROSS:
				return Cursor.CROSSHAIR_CURSOR;
			case CT_HAND:
				return Cursor.HAND_CURSOR;
			case CT_IBEAM:
				return Cursor.TEXT_CURSOR;
			case CT_WAIT:
				return Cursor.WAIT_CURSOR;
			case CT_HELP:
				return Cursor.HAND_CURSOR;
				
			case CT_EASTRESIZE:
				return Cursor.E_RESIZE_CURSOR;
			case CT_NORTHRESIZE:
				return Cursor.N_RESIZE_CURSOR;
			case CT_NORTHEASTRESIZE:
				return Cursor.NE_RESIZE_CURSOR;
			case CT_NORTHWESTRESIZE:
				return Cursor.NW_RESIZE_CURSOR;
			case CT_SOUTHRESIZE:
				return Cursor.S_RESIZE_CURSOR;
			case CT_SOUTHEASTRESIZE:
				return Cursor.SE_RESIZE_CURSOR;
			case CT_SOUTHWESTRESIZE:
				return Cursor.SW_RESIZE_CURSOR;
			case CT_WESTRESIZE:
				return Cursor.W_RESIZE_CURSOR;
				
			case CT_NORTHSOUTHRESIZE:
				return Cursor.MOVE_CURSOR;
			case CT_EASTWESTRESIZE:
				return Cursor.MOVE_CURSOR;
			case CT_NORTHEASTSOUTHWESTRESIZE:
				return Cursor.MOVE_CURSOR;
			case CT_NORTHWESTSOUTHEASTRESIZE:
				return Cursor.MOVE_CURSOR;
				
			case CT_COLUMNRESIZE:
				return Cursor.MOVE_CURSOR;
			case CT_ROWRESIZE:
				return Cursor.MOVE_CURSOR;
				
			case CT_MIDDLEPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_EASTPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_NORTHPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_NORTHEASTPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_NORTHWESTPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_SOUTHPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_SOUTHEASTPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_SOUTHWESTPANNING:
				return Cursor.MOVE_CURSOR;
			case CT_WESTPANNING:
				return Cursor.MOVE_CURSOR;
				
			case CT_MOVE:
				return Cursor.MOVE_CURSOR;
			case CT_VERTICALTEXT:
				return Cursor.TEXT_CURSOR;
			case CT_CELL:
				return Cursor.CROSSHAIR_CURSOR;
			case CT_CONTEXTMENU:
				return Cursor.CROSSHAIR_CURSOR;
			case CT_ALIAS :
				return Cursor.CROSSHAIR_CURSOR;
			case CT_PROGRESS:
				return Cursor.WAIT_CURSOR;
			case CT_NODROP:
				return Cursor.DEFAULT_CURSOR;
			case CT_COPY:
				return Cursor.DEFAULT_CURSOR;
			case CT_NONE:
				return Cursor.DEFAULT_CURSOR;
			case CT_NOTALLOWED:
				return Cursor.DEFAULT_CURSOR;
				
			case CT_ZOOMIN:
				return Cursor.DEFAULT_CURSOR;
			case CT_ZOOMOUT:
				return Cursor.DEFAULT_CURSOR;
				
			case CT_GRAB:
				return Cursor.HAND_CURSOR;
			case CT_GRABBING:
				return Cursor.MOVE_CURSOR;
				
			case CT_MIDDLE_PANNING_VERTICAL:
				return Cursor.DEFAULT_CURSOR;
			case CT_MIDDLE_PANNING_HORIZONTAL:
				return Cursor.DEFAULT_CURSOR;
				
			case CT_CUSTOM:
				return Cursor.CUSTOM_CURSOR;
				
			case CT_DND_NONE:
				return Cursor.DEFAULT_CURSOR;
			case CT_DND_MOVE:
				return Cursor.DEFAULT_CURSOR;
			case CT_DND_COPY:
				return Cursor.DEFAULT_CURSOR;
			case CT_DND_LINK :
				return Cursor.DEFAULT_CURSOR;
			}
			return -1;
		}
		
	};
	
	/**
	 * Creates a new direct bytebuffer. May throw some fatal memory exceptions when used incorrectly (address invalid, covering memory not owned by the application, etc...).
	 * @param address the memoryaddress
	 * @param capacity the capacity in bytes
	 * @return the direct bytebuffer
	 */
	final public static native ByteBuffer createNew(long address, int capacity);
	/**
	 * Initializes CEF. Should not be invoked, for that use CEFInstance.init(..).
	 * @param helperPath path to the helper executable
	 * @param frameworkpath path to the CEF framework (MACOS only)
	 * @param bundlepath path to the application bundle (MACOS only)
	 * @param cache path to the root cache location
	 * @param bcolor backgroundcolor 
	 * @param logsev logseverity, defined in CEFFunk
	 * @return reserved
	 */
	final public static native int init(String helperPath, String frameworkpath, String bundlepath, String cache, int bcolor, int logsev);
	/**
	 * Does the message loop work. Only invoke this method from the CEFInstance.instance instance of CEFInstance for global comparability-> if you are using any 3th party libraries using CEFJB or the CEFSwingComponent.
	 * Latest invoker will be the target of all callbacks.
	 */
	final public native void doMessageLoopWork();
	/**
	 * Closes CEF. Should not be invoked, for that use CEFInstance.free().
	 */
	final public static native void free();
	/**
	 * Creates a new browser.
	 * @param url url of the browser
	 * @return proxy-id of the browser
	 */
	final public static native int createBrowser(String url);
	/**
	 * Deletes a browser.
	 * @param id id of the browser
	 * @param force if the browser should be force-closed
	 */
	final public static native void deleteBrowser(int id, boolean force);
	/**
	 * Sends a mouse click to the browser.
	 * @param browser the browser to send to
	 * @param x x-position in pixels
	 * @param y y-position in pixels
	 * @param modifiers any modifiers
	 * @param type type of the click. May be one of [MBT_LEFT, MBT_RIGHT, MBT_MIDDLE]
	 * @param mouseUp false if the mouse is pressed down
	 * @param clickcount 
	 */
	final public static native void sendMouseClickEvent(int browser, int x, int y, int modifiers, int type, boolean mouseUp, int clickcount);
	/**
	 * Sends a key click to the browser.
	 * @param browser the browser to send to
	 * @param character character of the key
	 * @param modifiers any modifiers
	 * @param nativeKeyCode the native key code
	 * @param type the type of click, defined in CEFFunk
	 */
	final public static native void sendKeyEvent(int browser, char character, int modifiers, int nativeKeyCode, int type);
	/**
	 * Sends a mouse scroll event to the browser.
	 * @param browser the browser to send to
	 * @param x x-position in pixels
	 * @param y y-position in pixels
	 * @param modifiers any modifiers
	 * @param deltaX amount of x-scrolling
	 * @param deltaY amount of y-scrolling
	 */
	final public static native void sendMouseScrollEvent(int browser, int x, int y, int modifiers, int deltaX, int deltaY);
	/**
	 * Sends a mouse move event to the browser
	 * @param browser the browser to send to
	 * @param x x-position in pixels
	 * @param y y-position in pixels
	 * @param modifiers any modifiers
	 * @param mouseLeave
	 */
	final public static native void sendMouseMoveEvent(int browser, int x, int y, int modifiers, boolean mouseLeave);
	/**
	 * Send a capture lost event to the browser.
	 * @param browser the browser to send to
	 */
	final public static native void sendCaptureLostEvent(int browser);
	/**
	 * Issue a BeginFrame request to Chromium.
	 * @param browser the browser to send to
	 */
	final public static native void sendExternalBeginFrame(int browser);
	/**
	 * @param browser the affected browser
	 * @return if the browser can go back
	 */
	final public static native boolean canGoBack(int browser);
	/**
	 * @param browser the affected browser
	 * @return if the browser can go forward
	 */
	final public static native boolean canGoForward(int browser);
	/**
	 * Makes the browser go backward.
	 * @param browser the affected browser
	 */
	final public static native void goBack(int browser);
	/**
	 * Makes the browser go forward.
	 * @param browser the affected browser
	 */
	final public static native void goForward(int browser);
	/**
	 * Stops loading of the page.
	 * @param browser the affected browser
	 */
	final public static native void stopLoading(int browser);
	/**
	 * @param browser the affected browser
	 * @return if the browser is currently loading
	 */
	final public static native boolean isLoading(int browser);
	/**
	 * @param browser the affected browser
	 * @return if the browser has a document
	 */
	final public static native boolean hasDocument(int browser);
	/**
	 * Sets the framerate of the browser.
	 * @param browser the affected browser
	 * @param fps framerate
	 */
	final public static native void setFramerate(int browser, int fps);
	/**
	 * Reloads the browser.
	 * @param browser the affected browser
	 * @param ignoreCache
	 */
	final public static native void reload(int browser, boolean ignoreCache);
	
	/**
	 * Loads an url.
	 * @param browser the affected browser
	 * @param url 
	 */
	final public static native void loadURL(int browser, String url);
	/**
	 * @param browser the browser hosting the source
	 */
	final public static native void viewSource(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void cut(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void copy(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void paste(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void selectall(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void delete(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void undo(int browser);
	/**
	 * Keyboard shortcut.
	 * @param browser the affected browser
	 */
	final public static native void redo(int browser);
	/**
	 * @param browser the affected browser
	 * @param direction [CEF_ZOOM_COMMAND_IN, CEF_ZOOM_COMMAND_RESET, CEF_ZOOM_COMMAND_OUT]
	 * @return if the browser can zoom in a given direction
	 */
	final public static native boolean canZoom(int browser, int direction);
	/**
	 * Zooms in a given direction.
	 * @param browser the affected browser
	 * @param direction [CEF_ZOOM_COMMAND_IN, CEF_ZOOM_COMMAND_RESET, CEF_ZOOM_COMMAND_OUT]
	 */
	final public static native void zoom(int browser, int direction);
	/**
	 * Sets the zoom level.
	 * @param browser the affected browser
	 * @param level level of the zoom
	 */
	final public static native void setZoom(int browser, double level);
	/**
	 * Sets if the browser is currently focused.
	 * @param browser the affected browser
	 * @param focus if the browser is currently focused
	 */
	final public static native void setFocus(int browser, boolean focus);
	/**
	 * Notify the browser that it has been hidden or shown.
	 * @param browser the affected browser
	 * @param hidden if the browser is currently hidden
	 */
	final public static native void wasHidden(int browser, boolean hidden);
	/**
	 * Notify the browser that the widget has been resized.
	 * @param browser the affected browser
	 */
	final public static native void wasResized(int browser);
	/**
	 * Finds a string. The search will be restarted if 'tofind' or 'matchCase' change. The search will be stopped if 'tofind' is empty.
	 * @param browser the affected browser
	 * @param tofind string to find
	 * @param forward whether to search forward or backward within the page
	 * @param matchCase if the search should be case-sensitive
	 * @param findNext whether this is the first request of a follow-up
	 */
	final public static native void find(int browser, String tofind, boolean forward, boolean matchCase, boolean findNext);
	/**
	 * Cancel all searches that are currently going on.
	 * @param browser the affected browser
	 * @param clearSelection
	 */
	final public static native void stopFinding(int browser, boolean clearSelection);
	/**
	 * Invoked when the browser image is updated.
	 * @param browserID the affected browser
	 * @param buffer address of a bytebuffer containing the BGRA image data
	 * @param width width of the image
	 * @param height height of the image
	 */
	public void paint(int browserID, long buffer, int width, int height) {
		
	}
	public void audioStart(int browserID, int frames, int channelLayout, int sampleRate, int channels) {
		
	}
	public void audioPacket(int browserID, float[] data, int frames, long pts) {
			
	}
	public void audioStop(int browserID) {
		
	}
	public void audioError(int browserID, String message) {
		
	}
	public void cursorChange(int browserID, int cursor) {
		
	}
	public void loadingProcessChanged(int browserID, double d) {
	
	}
	public void tooltip(int browserID, String tooltip) {
		
	}
	public void fullscreenModeChanged(int browserID, boolean fullscreen) {
	
	}
	public void faviconChanged(int browserID, String[] urls) {
		
	}
	public void titleChanged(int browserID, String title) {
		
	}
	public void addressChanged(int browserID, String url) {
		
	}
	/**
	 * Returns the audio parameters.
	 * @return
	 */
	public int[] getAudioParam() {
		return new int[] {2, 2, 2};
	}
	/**
	 * Returns the preferred window size.
	 * @param browserID the affected browser
	 * @return [2]{width, height}
	 */
	public int[] windowSize(int browserID) {
		return new int[] {800, 600};
	}
	/**
	 * Log output of the CEFJB native
	 * @param message message
	 * @param type log type
	 */
	public void log(String message, int type) {
		
	}
}
