package Kartoffel.Licht;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class CEFInstance {
	
	/**
	 * Exception thrown on initialization.
	 */
	public static class OSNotSupportedException extends RuntimeException {
		private static final long serialVersionUID = 145627621761L;
		public OSNotSupportedException(String message) {
			super(message);
		}
	}
	/**
	 * An interface for the inbuilt auto-download system.
	 */
	public static interface NativeDownloadManager {
		/**
		 * Returns an archive (tar.bz2) containing the CEF sample application and all necessary libraries/resources to run it.
		 * @param isAMD false if the system is ARM
		 * @param is32 false if the system is 64bit
		 * @param os the lowercase os-string
		 * @return an inputstream linking to the archive
		 * @throws Exception if any exception occurs
		 */
		public InputStream getCefArchive(boolean isAMD, boolean is32, String os) throws Exception;
		/**
		 * Returns the raw data of the native part of CEFJB.
		 * @param isAMD false if the system is ARM
		 * @param is32 false if the system is 64bit
		 * @param os the lowercase os-string
		 * @return an inputstream linking to the raw binary data
		 * @throws Exception if any exception occurs
		 */
		public InputStream getNativeLib(boolean isAMD, boolean is32, String os) throws Exception;
		/**
		 * Returns the suffix of the library type of the OS (without dot).
		 * @param isAMD false if the system is ARM
		 * @param is32 false if the system is 64bit
		 * @param os the lowercase os-string
		 * @return the suffix of the library type of the OS
		 */
		public String getSuffix(boolean isAMD, boolean is32, String os);
		
	}
	/**
	 * Simple implementation of the NativeDownloadManager.
	 */
	public static class NativeDownloadManagerImpl implements NativeDownloadManager{
		
		/*
		 * Enum containing links to the builds of CEF. May change, may not, may get outdated, who knows?
		 */
		public static enum urls {
			
			WINDOWS64("https://cef-builds.spotifycdn.com/cef_binary_120.1.10%2Bg3ce3184%2Bchromium-120.0.6099.129_windows64_client.tar.bz2"),
			WINDOWSARM64("https://cef-builds.spotifycdn.com/cef_binary_120.1.10%2Bg3ce3184%2Bchromium-120.0.6099.129_windowsarm64_client.tar.bz2"),
			MAXOS64("https://cef-builds.spotifycdn.com/cef_binary_120.1.10%2Bg3ce3184%2Bchromium-120.0.6099.129_macosx64_client.tar.bz2"),
			MACOSARM64("https://cef-builds.spotifycdn.com/cef_binary_120.1.10%2Bg3ce3184%2Bchromium-120.0.6099.129_macosarm64_client.tar.bz2"),
			LINUX64("https://cef-builds.spotifycdn.com/cef_binary_120.1.10%2Bg3ce3184%2Bchromium-120.0.6099.129_linux64_client.tar.bz2"),
			LINUXARM64("https://cef-builds.spotifycdn.com/cef_binary_120.1.10%2Bg3ce3184%2Bchromium-120.0.6099.129_linuxarm64_client.tar.bz2");
			
			String url;
			urls(String s) {
				this.url = s;
			}
			
		}
		
		//For debugging
		InputStream getRAS(String name) {
			InputStream s = CEFInstance.class.getClassLoader().getResourceAsStream(name);
			if(s == null)
				s = CEFInstance.class.getClassLoader().getResourceAsStream("src/"+name);
			return s;
		}
		@Override
		public InputStream getCefArchive(boolean isAMD, boolean is32, String os) throws Exception {
			String system = os.contains("windows") ? "WINDOWS" : os.contains("Mac") ? "MACOS" : "LINUX";
			String url = urls.valueOf(system+(isAMD?"":"ARM")+"64").url;
			return new URL(url).openStream();
		}

		@Override
		public InputStream getNativeLib(boolean isAMD, boolean is32, String os) throws Exception {
			return getRAS("CEFJB."+getSuffix(isAMD, is32, os));
		}

		@Override
		public String getSuffix(boolean isAMD, boolean is32, String os) {
			return os.contains("windows") ? "dll" : os.contains("Mac") ? "dylib" : "so";
		}
		
	}
	
	private static boolean init = false;
	/**
	 * Amount of bytes read when downloading
	 */
	public static long bytesRead = 0;	
	
	public static final int[] audioParams = new int[] {1, 1, 1};
	
	private static final HashMap<Integer, CEFFunk> callbacks = new HashMap<Integer, CEFFunk>();
	public static final CEFFunk instance = new CEFFunk() {
		@Override
		public void audioError(int browserID, String message) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).audioError(browserID, message);
		}
		@Override
		public void audioPacket(int browserID, float[] data, int frames, long pts) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).audioPacket(browserID, data, frames, pts);
		}
		@Override
		public void audioStart(int browserID, int frames, int channelLayout, int sampleRate, int channels) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).audioStart(browserID, frames, channelLayout, sampleRate, channels);
		}
		@Override
		public void audioStop(int browserID) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).audioStop(browserID);
		}
		@Override
		public void log(String message, int type) {
			for(CEFFunk f : callbacks.values())
				f.log(message, type);
		}
		@Override
		public void paint(int browserID, long buffer, int width, int height) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).paint(browserID, buffer, width, height);
		}
		@Override
		public void titleChanged(int browserID, String title) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).titleChanged(browserID, title);
		}
		@Override
		public void addressChanged(int browserID, String address) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).addressChanged(browserID, address);
		}
		@Override
		public void cursorChange(int browserID, int cursor) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).cursorChange(browserID, cursor);
		}
		@Override
		public void faviconChanged(int browserID, String[] urls) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).faviconChanged(browserID, urls);
		}
		@Override
		public void loadingProcessChanged(int browserID, double d) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).loadingProcessChanged(browserID, d);
		}
		@Override
		public void fullscreenModeChanged(int browserID, boolean fullscreen) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).fullscreenModeChanged(browserID, fullscreen);
		}
		@Override
		public void tooltip(int browserID, String tooltip) {
			if(callbacks.get(browserID) != null)
				callbacks.get(browserID).tooltip(browserID, tooltip);
		}
		@Override
		public int[] getAudioParam() {
			return audioParams;
		}
		
		@Override
		public int[] windowSize(int browserID) {
			if(callbacks.get(browserID) != null)
				return callbacks.get(browserID).windowSize(browserID);
			return super.windowSize(browserID);
		}
		
	};
	
	/**
	 * Empty constructor for all your dreams come true >:)
	 */
	CEFInstance() {
		
	}
	/**
	 * Registers a CEFFunk. This instance will only receive events related to the browser
	 * @param browserID the browser
	 * @param callback the callback
	 */
	public static void register(int browserID, CEFFunk callback) {
		callbacks.put(browserID, callback);
	}
	/**
	 * Deregisters a CEFFunk instance.
	 * @param browserID
	 */
	public static void deregister(int browserID) {
		callbacks.remove(browserID);
	}
	
	/**
	 * Initializes CEFJB if not already.
	 * @param path a path to the "Release" folder containing the libraries. Has to end with a '/' or '\'
	 * @param subpathToHelperExecutable a path to a simple helper executable leading from 'path' (pathToHelperExecutable = path+subpathToExecutable)
	 */
	public static File init(String path, String subpathToHelperExecutable){
		if(!init) 
			return init(path, subpathToHelperExecutable, false, true, new NativeDownloadManagerImpl());
		return null;
	}
	/**
	 * Does the message loop work on the global instance.
	 */
	public static void doMessageLoopWork() {
		instance.doMessageLoopWork();
	}
	
	/**
	 * Closes CEF.
	 */
	public static void free() {
		CEFFunk.free();
	}
	
	/**
	 * Initializes CEFJB if not already.
	 * @param libpath a path to the "Release" folder containing the libraries. Has to end with a '/' or '\'
	 * @param subpathToHelperExecutable a path to a simple helper executable leading from 'path' (pathToHelperExecutable = path+subpathToExecutable)
	 * @param silent if log should be output using System.out/err
	 * @param autodownload if natives should automatically be downloaded using the downloadmanager
	 * @param downloadManager An interface for the inbuilt auto-download system
	 * @return a file pointing to the CEFJB native
	 */
	public static File init(String path, String subpathToHelperExecutable, boolean silent, boolean autodownload, NativeDownloadManager downloadManager) {
		if(init) {
			if(!silent)
				System.err.println("Already initialized!");
			return null;
		}
		String librarypath = path.replace("\\", "/");
		if(!librarypath.endsWith("/"))
			librarypath = librarypath+"/";
		String os = System.getProperty("os.name").toLowerCase();
		String system = os.contains("windows") ? ".dll" : os.contains("Mac") ? ".dylib" : ".so";
		File libraryFile = new File(librarypath+"Release/CEFJB"+system);
		File f = new File(librarypath);
		f.mkdirs();
		init = true;
		if(!libraryFile.exists())
			if(autodownload) {
				if(!silent)
					System.out.println("No Library found, downloading...");
				try {
					download(f, silent, downloadManager);
				} catch (Exception e) {
					if(!silent)
						System.err.println("Failed to auto-download library! " + e.getMessage());
					e.printStackTrace();
					return null;
				}
			}
			else
				if(!silent)
					System.out.println("No Library found. Not downloading.");
		try {
			//Order of library loading is important!
			System.load(librarypath+"Release/vulkan-1"+system);
			System.load(librarypath+"Release/vk_swiftshader"+system);
			System.load(librarypath+"Release/libGLESv2"+system);
			System.load(librarypath+"Release/libEGL"+system);
			System.load(librarypath+"Release/d3dcompiler_47"+system);
			System.load(librarypath+"Release/chrome_elf"+system);
			System.load(librarypath+"Release/libcef"+system);
			System.load(libraryFile.getAbsolutePath());
			CEFFunk.init(librarypath+"Release/"+subpathToHelperExecutable, "", "", "", 0, CEFFunk.LOGSEVERITY_VERBOSE);
			init = true;
		} catch (Exception e) {
			if(!silent)
				System.err.println("Failed to load Libraries! " + e.getMessage());
		}
		doMessageLoopWork();
		return libraryFile;
	}
	
	/**
	 * Force-downloads all required natives and stores them to dir. Use CEFInstance.init(..) for checks, loading using System.load, etc...
	 * @param dir the directory to download to
	 * @param silent if log should be output using System.out/err
	 * @param downloadManager An interface for the inbuilt auto-download system
	 * @throws Exception if any exception occurs
	 */
	public static void download(File dir, boolean silent, NativeDownloadManager downloadManager) throws Exception {
		String a = System.getProperty("os.arch");
		String os = System.getProperty("os.name").toLowerCase();
		boolean is64 = a.contains("64");
		boolean isAMD = a.contains("amd");
		
		String system_suffix = downloadManager.getSuffix(isAMD, !is64, os);
		
		new File(dir.getAbsolutePath()+"/Release/").mkdirs();
		
		{
			InputStream is = downloadManager.getNativeLib(isAMD, !is64, os);
			if(is == null)
				throw new OSNotSupportedException("Downloadmanager returned null(NativeLib)!");
			is = new BufferedInputStream(is);
			File ff = new File(dir.getAbsolutePath()+"/Release/CEFJB."+system_suffix);
			ff.createNewFile();
			FileOutputStream fos = new FileOutputStream(ff);
			fos.write(is.readAllBytes());
			fos.flush();
			fos.close();
			is.close();
		}
		{
			InputStream is = downloadManager.getCefArchive(isAMD, !is64, os);
			if(is == null)
				throw new OSNotSupportedException("Downloadmanager returned null(CefArchive)!");
			is = new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(is));
			
			TarArchiveInputStream is2 = new TarArchiveInputStream(is);
			TarArchiveEntry ae = is2.getNextTarEntry();
			while(ae != null) {
				String path = ae.getName().substring(ae.getName().indexOf("/")+1);
				if(!silent)
					System.out.println(dir.getAbsolutePath()+"/"+path);
				File f = new File(dir.getAbsolutePath()+"/"+path); //Skips forward
				if(ae.isDirectory())
					f.mkdirs();
				else {
					f.createNewFile();
					FileOutputStream fis = new FileOutputStream(f);
					if(!silent)
						System.out.println("Extracting File: " + f.getName() + " (" + ae.getSize()/1000.0 + "KB)");
					byte[] data = is2.readNBytes((int) ae.getSize());
					fis.write(data);
					fis.flush();
					fis.close();
					if(!silent)
						System.out.println("<--Saved File: " + f.getName() );
				}
			
			
				ae = is2.getNextTarEntry();
			}
			is.close();
			is2.close();
		}
	}

}
