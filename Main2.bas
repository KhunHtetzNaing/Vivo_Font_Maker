Type=Activity
Version=6.5
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Dim cc As ContentChooser
	Dim cf As ContentChooser
	Dim ad,ad1 As Timer
End Sub

Sub Globals
Dim iv As ImageView
Dim b1,b2,b3 As Button
Dim zip As ABZipUnzip
Dim st As String
Dim lb As Label
	Dim copy As BClipboard
	Dim Banner As AdView
	Dim Interstitial As InterstitialAd
End Sub

Sub Activity_Create(FirstTime As Boolean)
	
	Activity.Title = "Font2Txj"
	Banner.Initialize("Banner","ca-app-pub-4173348573252986/5620794951")
	Dim height As Int
	If GetDeviceLayoutValues.ApproximateScreenSize < 6 Then
		'phones
		If 100%x > 100%y Then height = 32dip Else height = 50dip
	Else
		'tablets
		height = 90dip
	End If
	Activity.AddView(Banner, 0dip, 100%y - height, 100%x, height)
	Banner.LoadAd
	Log(Banner)
	
	Interstitial.Initialize("Interstitial","ca-app-pub-4173348573252986/1050994551")
	Interstitial.LoadAd
	
	ad.Initialize("ad",60000)
	ad.Enabled = True
	
	ad1.Initialize("ad1",100)
	ad1.Enabled = False
	
	Activity.AddMenuItem3("Change Font","change",LoadBitmap(File.DirAssets,"change.png"),True)
	Activity.AddMenuItem3("Share App","share",LoadBitmap(File.DirAssets,"share.png"),True)
	
	cc.Initialize("cc")
	cf.Initialize("cf")
iv.Initialize("iv")
	iv.Bitmap = LoadBitmap(File.DirRootExternal & "/Vivo Font Maker/Project","HtetzNaing_preview.jpg")
	iv.Gravity = Gravity.FILL
	Activity.AddView(iv,10%x,0%y,80%x,50%y)
	
	b1.Initialize("b1")
	b1.Text = "Change Preview Photo"
	
	b2.Initialize("b2")
	b2.Text = "Pick Font"
	
	b3.Initialize("b3")
	b3.Text = "Create Vivo Font"
	
	lb.Initialize("lb")
	lb.Text = "[Please!! Choose Font File eg: MyFont.ttf ]"
	lb.Gravity = Gravity.CENTER
	Activity.AddView(b1,20%x,(iv.Top+iv.Height)+3%y,60%x,10%y)
	Activity.AddView(lb,0%x,(b1.Top+b1.Height),100%x,5%y)
	Activity.AddView(b2,20%x,(lb.Top+lb.Height),60%x,10%y)
	Activity.AddView(b3,20%x,(b2.Top+b2.Height),60%x,10%y)
End Sub

Sub iv_Click
	Dim i As Intent
	i.Initialize(i.ACTION_VIEW,"file:///" & File.DirRootExternal & "/Vivo Font Maker/Project/HtetzNaing_preview.jpg")
	i.SetType("image/*")
	StartActivity(i)
End Sub

Sub b1_Click
	cc.Show("image/*","Choose image")
End Sub

Sub b2_Click
	cf.Show("*/*","Choose Font")
End Sub

Sub b3_Click
	Dim sd As String = File.DirRootExternal & "/"
	st = File.ReadString(File.DirRootExternal & "/Vivo Font Maker","name.txt")
	Msgbox("Vivo font file will create a new file name with " &st&".txj in" &CRLF& "sdcard/Vivo Font Maker/Output/","Attention!")
	
	zip.ABZipDirectory(sd & "Vivo Font Maker/Project",sd & "Vivo Font Maker/Output/"&st&".txj")
	ad1.Enabled = True
	If File.Exists(File.DirRootExternal & "/Vivo Font Maker/Output",st&".txj") = True Then
		Msgbox(st&".txj file created successfully in"&CRLF&"sdcard/Vivo Font Maker/Output/","Completed!")
	Else
		Msgbox("error","")
	End If
	
'	File.Delete(File.DirRootExternal & "/Vivo Font Maker/Project",st&".xml")
'	File.Delete(File.DirRootExternal & "/Vivo Font Maker","name.txt")
End Sub

Sub cc_Result (Success As Boolean, Dir As String, FileName As String)
	st = File.ReadString(File.DirRootExternal & "/Vivo Font Maker","name.txt")
	If Success Then	
		File.Delete(File.DirRootExternal & "/Vivo Font Maker/Project","HtetzNaing_preview.jpg")
		File.Copy(Dir, FileName, File.DirRootExternal & "/Vivo Font Maker/Project", st&"_preview.jpg")
		iv.RemoveView
		iv.Bitmap = LoadBitmap(File.DirRootExternal & "/Vivo Font Maker/Project",st&"_preview.jpg")
		Activity.AddView(iv,10%x,0%y,80%x,50%y)
	End If
End Sub

Sub cf_Result (Success As Boolean, Dir As String, FileName As String)
	st = File.ReadString(File.DirRootExternal & "/Vivo Font Maker","name.txt")
	Log(Dir & FileName)
	If Success Then
		If FileName.EndsWith(".ttf") = False Then
			Msgbox("Your font is not TrueType Font(.ttf)"&CRLF&"Firstly!! Please convert your font to .ttf","Error")
			Else
			File.Delete(File.DirRootExternal & "/Vivo Font Maker/Project","HtetzNaing.ttf")
			File.Copy(Dir, FileName, File.DirRootExternal & "/Vivo Font Maker/Project", st&".ttf")
		End If
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub change_Click
Dim p As PhoneIntents
	StartActivity(p.OpenBrowser("http://www.htetznaing.com"))
End Sub

Sub share_Click
	ad1.Enabled = True
	Dim ShareIt As Intent
	copy.clrText
	copy.setText("You can change easily any font you like into Vivo font (.txj)"&CRLF&"[Features/] "&CRLF&"Font Name!"&CRLF&"Author Name!"&CRLF&"Version!"&CRLF&"Preview Image!"&CRLF&"Note: You can convert TrueType Font(.ttf) To .txj only!!!"&CRLF&"( Other font extension Not working, eg: otf,woff,ofm,eot)"&CRLF&"Download Free at here : http://www.htetznaing.com/search?q=Vivo+Font+Maker")
	ShareIt.Initialize (ShareIt.ACTION_SEND,"")
	ShareIt.SetType ("text/plain")
	ShareIt.PutExtra ("android.intent.extra.TEXT",copy.getText)
	ShareIt.PutExtra ("android.intent.extra.SUBJECT","Get Free!!")
	ShareIt.WrapAsIntentChooser("Share App Via...")
	StartActivity (ShareIt)
End Sub

Sub Interstitial_AdClosed
	Interstitial.LoadAd
End Sub

Sub ad_Tick
	If Interstitial.Ready Then Interstitial.Show
End Sub

Sub ad1_Tick
	If Interstitial.Ready Then Interstitial.Show
	ad1.Enabled = False
End Sub