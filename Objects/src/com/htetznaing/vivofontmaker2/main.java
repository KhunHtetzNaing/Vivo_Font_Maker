package com.htetznaing.vivofontmaker2;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.vivofontmaker2", "com.htetznaing.vivofontmaker2.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.vivofontmaker2", "com.htetznaing.vivofontmaker2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.vivofontmaker2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _ad = null;
public static anywheresoftware.b4a.objects.Timer _ad1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edt = null;
public anywheresoftware.b4a.objects.EditTextWrapper _eda = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edv = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b = null;
public com.AB.ABZipUnzip.ABZipUnzip _zip = null;
public static String _st = "";
public static String _sa = "";
public static String _sv = "";
public static String _rd = "";
public static String _rp = "";
public static String _d = "";
public b4a.util.BClipboard _copy = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _interstitial = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb = null;
public com.htetznaing.vivofontmaker2.main2 _main2 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (main2.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
int _height = 0;
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="lb.Initialize(\"lb\")";
mostCurrent._lb.Initialize(mostCurrent.activityBA,"lb");
 //BA.debugLineNum = 38;BA.debugLine="lb.Text = \"Powered By Myanmar Android Apps\"";
mostCurrent._lb.setText((Object)("Powered By Myanmar Android Apps"));
 //BA.debugLineNum = 39;BA.debugLine="Activity.AddView(lb,0%x,100%y - 100dip,100%x,50di";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (100))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 40;BA.debugLine="lb.Gravity = Gravity.CENTER";
mostCurrent._lb.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 42;BA.debugLine="Activity.Title = \"Vivo Font Maker\"";
mostCurrent._activity.setTitle((Object)("Vivo Font Maker"));
 //BA.debugLineNum = 43;BA.debugLine="Activity.AddMenuItem3(\"Share App\",\"share\",LoadBit";
mostCurrent._activity.AddMenuItem3((java.lang.CharSequence)("Share App"),"share",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"share.png").getObject()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 45;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-4173348573";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/5620794951");
 //BA.debugLineNum = 46;BA.debugLine="Dim height As Int";
_height = 0;
 //BA.debugLineNum = 47;BA.debugLine="If GetDeviceLayoutValues.ApproximateScreenSize <";
if (anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()<6) { 
 //BA.debugLineNum = 49;BA.debugLine="If 100%x > 100%y Then height = 32dip Else height";
if (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)>anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)) { 
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32));}
else {
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50));};
 }else {
 //BA.debugLineNum = 52;BA.debugLine="height = 90dip";
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (90));
 };
 //BA.debugLineNum = 54;BA.debugLine="Activity.AddView(Banner, 0dip, 100%y - height, 10";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-_height),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_height);
 //BA.debugLineNum = 55;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 56;BA.debugLine="Log(Banner)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(mostCurrent._banner));
 //BA.debugLineNum = 58;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/1050994551");
 //BA.debugLineNum = 59;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 61;BA.debugLine="ad.Initialize(\"ad\",60000)";
_ad.Initialize(processBA,"ad",(long) (60000));
 //BA.debugLineNum = 62;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 64;BA.debugLine="ad1.Initialize(\"ad1\",100)";
_ad1.Initialize(processBA,"ad1",(long) (100));
 //BA.debugLineNum = 65;BA.debugLine="ad1.Enabled = False";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 67;BA.debugLine="If File.Exists(File.DirRootExternal & \"/Vivo Font";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker/Output","")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 68;BA.debugLine="File.MakeDir(File.DirRootExternal,\"Vivo Font Mak";
anywheresoftware.b4a.keywords.Common.File.MakeDir(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"Vivo Font Maker/Output");
 };
 //BA.debugLineNum = 71;BA.debugLine="File.Copy(File.DirAssets,\"HtetzNaing.txj\",File.Di";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"HtetzNaing.txj",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker","HtetzNaing.zip");
 //BA.debugLineNum = 72;BA.debugLine="File.Copy(File.DirAssets,\"d.txt\",File.DirRootExte";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"d.txt",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker","d.txt");
 //BA.debugLineNum = 74;BA.debugLine="zip.ABUnzip(File.DirRootExternal & \"/Vivo Font Ma";
mostCurrent._zip.ABUnzip(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker/HtetzNaing.zip",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker/Project");
 //BA.debugLineNum = 75;BA.debugLine="File.Delete(File.DirRootExternal &  \"/Vivo Font M";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker","HtetzNaing.zip");
 //BA.debugLineNum = 77;BA.debugLine="edt.Initialize(\"edt\")";
mostCurrent._edt.Initialize(mostCurrent.activityBA,"edt");
 //BA.debugLineNum = 78;BA.debugLine="edt.Hint = \"Title\"";
mostCurrent._edt.setHint("Title");
 //BA.debugLineNum = 80;BA.debugLine="eda.Initialize(\"eda\")";
mostCurrent._eda.Initialize(mostCurrent.activityBA,"eda");
 //BA.debugLineNum = 81;BA.debugLine="eda.Hint = \"Author\"";
mostCurrent._eda.setHint("Author");
 //BA.debugLineNum = 83;BA.debugLine="edv.Initialize(\"edv\")";
mostCurrent._edv.Initialize(mostCurrent.activityBA,"edv");
 //BA.debugLineNum = 84;BA.debugLine="edv.Hint = \"Version\"";
mostCurrent._edv.setHint("Version");
 //BA.debugLineNum = 85;BA.debugLine="edv.InputType = edv.INPUT_TYPE_DECIMAL_NUMBERS";
mostCurrent._edv.setInputType(mostCurrent._edv.INPUT_TYPE_DECIMAL_NUMBERS);
 //BA.debugLineNum = 87;BA.debugLine="Activity.AddView(edt,10%x,1%y,80%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._edt.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 88;BA.debugLine="Activity.AddView(eda,10%x,(edt.Height+edt.Top)+1%y";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._eda.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA),(int) ((mostCurrent._edt.getHeight()+mostCurrent._edt.getTop())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 89;BA.debugLine="Activity.AddView(edv,10%x,(eda.Height+eda.Top)+1%";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._edv.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA),(int) ((mostCurrent._eda.getHeight()+mostCurrent._eda.getTop())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 91;BA.debugLine="b.Initialize(\"b\")";
mostCurrent._b.Initialize(mostCurrent.activityBA,"b");
 //BA.debugLineNum = 92;BA.debugLine="b.Text = \" Next\"";
mostCurrent._b.setText((Object)(" Next"));
 //BA.debugLineNum = 93;BA.debugLine="Activity.AddView(b,20%x,(edv.Top+edv.Height)+5%y,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._edv.getTop()+mostCurrent._edv.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _answ = 0;
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 178;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 179;BA.debugLine="Dim Answ As Int";
_answ = 0;
 //BA.debugLineNum = 180;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 181;BA.debugLine="Answ = Msgbox2(\"If you want to get new updates o";
_answ = anywheresoftware.b4a.keywords.Common.Msgbox2("If you want to get new updates on  Facebook? Please Like "+anywheresoftware.b4a.keywords.Common.CRLF+"Myanmar Android Apps Page!","Attention!","Yes","","No",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fb.png").getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 182;BA.debugLine="If Answ = DialogResponse.NEGATIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 183;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 186;BA.debugLine="If Answ = DialogResponse.POSITIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 187;BA.debugLine="Try";
try { //BA.debugLineNum = 189;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 191;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb:/";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 192;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 196;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 197;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://m.facebook";
_i.Initialize(_i.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 199;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 202;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 204;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 161;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 163;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 159;BA.debugLine="End Sub";
return "";
}
public static String  _ad_tick() throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub ad_Tick";
 //BA.debugLineNum = 154;BA.debugLine="If Interstitial.Ready Then Interstitial.Show";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();};
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _ad1_tick() throws Exception{
 //BA.debugLineNum = 206;BA.debugLine="Sub ad1_Tick";
 //BA.debugLineNum = 207;BA.debugLine="If Interstitial.Ready Then Interstitial.Show Else";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();}
else {
mostCurrent._interstitial.LoadAd();};
 //BA.debugLineNum = 208;BA.debugLine="ad1.Enabled = False";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 209;BA.debugLine="End Sub";
return "";
}
public static String  _b_click() throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub b_Click";
 //BA.debugLineNum = 116;BA.debugLine="If edt.Text = \"\" Then";
if ((mostCurrent._edt.getText()).equals("")) { 
 //BA.debugLineNum = 117;BA.debugLine="st = \"Vivo Font\"";
mostCurrent._st = "Vivo Font";
 }else {
 //BA.debugLineNum = 119;BA.debugLine="st = edt.Text";
mostCurrent._st = mostCurrent._edt.getText();
 };
 //BA.debugLineNum = 123;BA.debugLine="If eda.Text = \"\" Then";
if ((mostCurrent._eda.getText()).equals("")) { 
 //BA.debugLineNum = 124;BA.debugLine="sa = \"Khun Htetz Naing\"";
mostCurrent._sa = "Khun Htetz Naing";
 }else {
 //BA.debugLineNum = 126;BA.debugLine="sa = eda.Text";
mostCurrent._sa = mostCurrent._eda.getText();
 };
 //BA.debugLineNum = 130;BA.debugLine="If edv.Text = \"\" Then";
if ((mostCurrent._edv.getText()).equals("")) { 
 //BA.debugLineNum = 131;BA.debugLine="sv = \"1.0\"";
mostCurrent._sv = "1.0";
 }else {
 //BA.debugLineNum = 133;BA.debugLine="sv = edv.Text";
mostCurrent._sv = mostCurrent._edv.getText();
 };
 //BA.debugLineNum = 136;BA.debugLine="rd = \"<?xml version=@1.0@ encoding=@utf-8@?>\" &CR";
mostCurrent._rd = "<?xml version=@1.0@ encoding=@utf-8@?>"+anywheresoftware.b4a.keywords.Common.CRLF+"<bbkfont><name>"+mostCurrent._st+"</name>"+anywheresoftware.b4a.keywords.Common.CRLF+"	<author>"+mostCurrent._sa+"</author>"+anywheresoftware.b4a.keywords.Common.CRLF+"<File>"+mostCurrent._st+".ttf</File>"+anywheresoftware.b4a.keywords.Common.CRLF+"<id>"+mostCurrent._sv+"</id>"+anywheresoftware.b4a.keywords.Common.CRLF+"</bbkfont>";
 //BA.debugLineNum = 139;BA.debugLine="d = File.ReadString(File.DirRootExternal & \"/Vivo";
mostCurrent._d = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker","d.txt");
 //BA.debugLineNum = 140;BA.debugLine="rp = rd.Replace(\"@\",d)";
mostCurrent._rp = mostCurrent._rd.replace("@",mostCurrent._d);
 //BA.debugLineNum = 141;BA.debugLine="File.Delete(File.DirRootExternal & \"/Vivo Font Ma";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker/Project","HtetzNaing.xml");
 //BA.debugLineNum = 142;BA.debugLine="File.WriteString(File.DirRootExternal & \"/Vivo Fo";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker/Project",mostCurrent._st+".xml",mostCurrent._rp);
 //BA.debugLineNum = 143;BA.debugLine="File.WriteString(File.DirRootExternal & \"/Vivo Fo";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/Vivo Font Maker","name.txt",mostCurrent._st);
 //BA.debugLineNum = 144;BA.debugLine="Log(rp)";
anywheresoftware.b4a.keywords.Common.Log(mostCurrent._rp);
 //BA.debugLineNum = 145;BA.debugLine="StartActivity(Main2)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main2.getObject()));
 //BA.debugLineNum = 146;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim edt,eda,edv As EditText";
mostCurrent._edt = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._eda = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._edv = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim b As Button";
mostCurrent._b = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim zip As ABZipUnzip";
mostCurrent._zip = new com.AB.ABZipUnzip.ABZipUnzip();
 //BA.debugLineNum = 26;BA.debugLine="Dim st,sa,sv As String";
mostCurrent._st = "";
mostCurrent._sa = "";
mostCurrent._sv = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim rd,rp As String";
mostCurrent._rd = "";
mostCurrent._rp = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim d As String";
mostCurrent._d = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim copy As BClipboard";
mostCurrent._copy = new b4a.util.BClipboard();
 //BA.debugLineNum = 31;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim Interstitial As InterstitialAd";
mostCurrent._interstitial = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim lb As Label";
mostCurrent._lb = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _interstitial_adclosed() throws Exception{
 //BA.debugLineNum = 149;BA.debugLine="Sub Interstitial_AdClosed";
 //BA.debugLineNum = 150;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _interstitial_adopened() throws Exception{
 //BA.debugLineNum = 221;BA.debugLine="Sub Interstitial_adopened";
 //BA.debugLineNum = 222;BA.debugLine="End Sub";
return "";
}
public static String  _interstitial_failedtoreceivead(String _errorcode) throws Exception{
 //BA.debugLineNum = 216;BA.debugLine="Sub Interstitial_FailedToReceiveAd (ErrorCode As S";
 //BA.debugLineNum = 217;BA.debugLine="Log(\"not Received - \" &\"Error Code: \"&ErrorCode)";
anywheresoftware.b4a.keywords.Common.Log("not Received - "+"Error Code: "+_errorcode);
 //BA.debugLineNum = 218;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 219;BA.debugLine="End Sub";
return "";
}
public static String  _interstitial_receivead() throws Exception{
 //BA.debugLineNum = 212;BA.debugLine="Sub Interstitial_ReceiveAd";
 //BA.debugLineNum = 213;BA.debugLine="Log(\"Received\")";
anywheresoftware.b4a.keywords.Common.Log("Received");
 //BA.debugLineNum = 214;BA.debugLine="End Sub";
return "";
}
public static String  _lb_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 96;BA.debugLine="Sub lb_Click";
 //BA.debugLineNum = 97;BA.debugLine="Try";
try { //BA.debugLineNum = 99;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 101;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb://";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 102;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); //BA.debugLineNum = 106;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 107;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://m.facebook.";
_i.Initialize(_i.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 109;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
main2._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim ad,ad1 As Timer";
_ad = new anywheresoftware.b4a.objects.Timer();
_ad1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _share_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _shareit = null;
 //BA.debugLineNum = 165;BA.debugLine="Sub share_Click";
 //BA.debugLineNum = 166;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 167;BA.debugLine="Dim ShareIt As Intent";
_shareit = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 168;BA.debugLine="copy.clrText";
mostCurrent._copy.clrText(mostCurrent.activityBA);
 //BA.debugLineNum = 169;BA.debugLine="copy.setText(\"You can change easily any font you";
mostCurrent._copy.setText(mostCurrent.activityBA,"You can change easily any font you like into Vivo font (.txj)"+anywheresoftware.b4a.keywords.Common.CRLF+"[Features/] "+anywheresoftware.b4a.keywords.Common.CRLF+"Font Name!"+anywheresoftware.b4a.keywords.Common.CRLF+"Author Name!"+anywheresoftware.b4a.keywords.Common.CRLF+"Version!"+anywheresoftware.b4a.keywords.Common.CRLF+"Preview Image!"+anywheresoftware.b4a.keywords.Common.CRLF+"Note: You can convert TrueType Font(.ttf) To .txj only!!!"+anywheresoftware.b4a.keywords.Common.CRLF+"( Other font extension Not working, eg: otf,woff,ofm,eot)"+anywheresoftware.b4a.keywords.Common.CRLF+"Download Free at here : http://www.htetznaing.com/search?q=Vivo+Font+Maker");
 //BA.debugLineNum = 170;BA.debugLine="ShareIt.Initialize (ShareIt.ACTION_SEND,\"\")";
_shareit.Initialize(_shareit.ACTION_SEND,"");
 //BA.debugLineNum = 171;BA.debugLine="ShareIt.SetType (\"text/plain\")";
_shareit.SetType("text/plain");
 //BA.debugLineNum = 172;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.TEXT\",cop";
_shareit.PutExtra("android.intent.extra.TEXT",(Object)(mostCurrent._copy.getText(mostCurrent.activityBA)));
 //BA.debugLineNum = 173;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.SUBJECT\",";
_shareit.PutExtra("android.intent.extra.SUBJECT",(Object)("Get Free!!"));
 //BA.debugLineNum = 174;BA.debugLine="ShareIt.WrapAsIntentChooser(\"Share App Via...\")";
_shareit.WrapAsIntentChooser("Share App Via...");
 //BA.debugLineNum = 175;BA.debugLine="StartActivity (ShareIt)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_shareit.getObject()));
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
}
