package com.example.HOMEIOT.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.HOMEIOT.Mqtt
import com.example.HOMEIOT.databinding.FragmentSecureCameraBinding


import com.github.niqdev.mjpeg.DisplayMode
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Exception

private const val PUB_TOPIC1="iot/camera/angle"
private const val SERVER_URI1="tcp://192.168.219.104:1883"
//컴주소

class SecureCameraFragment : Fragment() {

    private var _binding: FragmentSecureCameraBinding? = null
    private val binding get() = _binding!!  //뷰바인딩
    lateinit var mqttClient : Mqtt

    override fun onAttach(context: Context) {
        super.onAttach(context)


        mqttClient=Mqtt(context, SERVER_URI1)
        try{
            mqttClient.setCallback(::onReceived)
            mqttClient.connect(arrayOf<String>(SUB_TOPIC))
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecureCameraBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShot.setOnClickListener{
            binding.mjpeg.visibility= View.INVISIBLE
            binding.imageView.visibility=View.VISIBLE
            val url="http://192.168.219.106:8000/mjpeg/snapshot"
            Glide.with(this)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imageView)
        }

        binding.btnStream.setOnClickListener{
            binding.mjpeg.visibility=View.VISIBLE
            binding.imageView.visibility=View.INVISIBLE

            Mjpeg.newInstance()
                .open("http://192.168.219.106:8000/mjpeg/stream",5)
                .subscribe{inputStream: MjpegInputStream?->
                    binding.mjpeg.setSource(inputStream!!)
                    binding.mjpeg.setDisplayMode(DisplayMode.BEST_FIT)
                }



        }
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(view: SeekBar?, value: Int, fromUser: Boolean) {
                if(fromUser){
                    binding.txtAngle.text="카메라 각도: $value"

                    //Seekbar의 값을 publish
                    mqttClient.publish(PUB_TOPIC1,"$value")
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onPause() {
        super.onPause()
        if(binding.mjpeg.isStreaming)
            binding.mjpeg.stopPlayback()
    }
    fun onReceived(topic:String,message: MqttMessage){
        val msg=String(message.payload)
        Log.i("수신", "수신메시지: $msg ")

    }


}


