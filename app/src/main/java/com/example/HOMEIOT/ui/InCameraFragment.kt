package com.example.HOMEIOT.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.HOMEIOT.Mqtt
import com.example.HOMEIOT.OpenWeather
import com.example.HOMEIOT.R
import com.example.HOMEIOT.databinding.FragmentDeviceControlBinding
import com.example.HOMEIOT.databinding.FragmentInCameraBinding


import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Exception


const val PUB_TOPIC_record="iot/camera/record"
const val PUB_TOPIC_capture="iot/camera/capture"
//const val SERVER_URI_camera="tcp://192.168.219.104:1883"
//컴주소
class InCameraFragment : Fragment() {

    private var _binding: FragmentInCameraBinding? = null
    private val binding get() = _binding!!  //뷰바인딩

    val TAG="MqttActivity"
    lateinit var mqttClient : Mqtt

//    override fun onCreate(savedInstanceState: Bundle?)
//        super.onCreate(savedInstanceState)




    override fun onAttach(context: Context) {//ctx를 넘겨줌
        super.onAttach(context)
        mqttClient=Mqtt(context, SERVER_URI)
        try{
            mqttClient.setCallback(::onReceived)
            mqttClient.connect(arrayOf<String>(SUB_TOPIC))
            //그냥 커넥트?라는데..
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        _binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
        _binding = FragmentInCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRecord.setOnCheckedChangeListener{ buttonview, isChecked ->
            if (isChecked){
                mqttClient.publish(PUB_TOPIC_record,"on")
                Toast.makeText(this.context,"녹화를 시작하겠습니다",Toast.LENGTH_SHORT).show()
                binding.btnRecord.setBackgroundDrawable(resources.getDrawable(R.drawable.stopbutton))
            }
            else{
                mqttClient.publish(PUB_TOPIC_record,"off")
                Toast.makeText(this.context,"녹화가 종료되었습니다.",Toast.LENGTH_SHORT).show()
                binding.btnRecord.setBackgroundDrawable(resources.getDrawable(R.drawable.record))
            }



        }
        binding.btnCapture.setOnClickListener{
                mqttClient.publish(PUB_TOPIC_capture,"captured")
                Toast.makeText(this.context,"캡쳐가 완료되었습니다.",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onReceived(topic:String,message: MqttMessage){
        val msg=String(message.payload)
        Log.i("mqtt", "수신메시지: $msg ")

    }
    fun publish(){
        mqttClient.publish(PUB_TOPIC_led,"1")
        mqttClient.publish(PUB_TOPIC_blind,"1")
    }

}