package com.zksr.testflaver

import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.zksr.testflaver.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClick()

        XXPermissions.with(this)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .request { _, all ->
                if (all){
                    Log.w(TAG,"权限已全部同意")
                }else{
                    Log.w(TAG,"部分或者全部权限被拒绝")
                }
            }

        loadOld()
    }

    private fun onClick() {
        binding.tvReadFilePath.setOnClickListener { getPath() }
        binding.tvCompress.setOnClickListener { compressPicture() }
    }

    private fun compressPicture(){
        val option = BitmapFactory.Options()
        //只读宽高 不加载图片 避免大图片oom
        option.inJustDecodeBounds = true
        val path = getPath()+"/launcher_ic.png"
        val file = BitmapFactory.decodeFile(path,option)
        if (file == null){
            Log.w(TAG,"file 是空的 没有返回图片 返回的是图片的宽高 w:${option.outWidth} h:${option.outHeight}")
        }
        option.inSampleSize = calculateInSampleSize(option,50,50)
        option.inJustDecodeBounds = false
        val reallyBitmap = BitmapFactory.decodeFile(path,option)
        if (reallyBitmap == null){
            Log.w("AAA","reallyBitmap == null")
        }else{
            binding.ivNewBitmap.setImageBitmap(reallyBitmap)
        }
    }

    private fun calculateInSampleSize(option: BitmapFactory.Options, with: Int, height: Int) : Int {
        val oldWith     = option.outWidth
        val oldHeight   = option.outHeight
        var inSampleSize = 1
        if (oldWith > with || oldHeight > height){
            val oldWithRatio = (oldWith / with.toDouble()).roundToInt()
            val oldHeightRatio = (oldHeight / height.toDouble()).roundToInt()
            inSampleSize = if (oldHeightRatio > oldWithRatio) oldWithRatio else oldWithRatio
            val totalBitmapPixels = oldHeight * oldWith
            val targetBitmapPixels = with * height * 2
            while (totalBitmapPixels / (inSampleSize * inSampleSize) > targetBitmapPixels){
                inSampleSize ++
            }
        }
        return inSampleSize
    }

    private fun getPath(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
        } else {
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path ?: ""
        }
    }

    private fun loadOld(){
        val oldBitmap = BitmapFactory.decodeFile(getPath()+"/launcher_ic.png")
        binding.ivOldBitmap.setImageBitmap(oldBitmap)
    }
}