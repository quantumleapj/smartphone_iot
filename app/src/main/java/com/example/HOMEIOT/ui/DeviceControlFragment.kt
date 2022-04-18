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
import com.example.HOMEIOT.Mqtt
import com.example.HOMEIOT.OpenWeather
import com.example.HOMEIOT.databinding.FragmentDeviceControlBinding


import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Exception

const val SUB_TOPIC="iot/#"
const val PUB_TOPIC_led="iot/led"
const val PUB_TOPIC_led_livingroom="iot/led/livingroom"
const val PUB_TOPIC_led_kitchen="iot/led/kitchen"
const val PUB_TOPIC_led_mainroom="iot/led/mainroom"
const val PUB_TOPIC_blind="iot/blind"
const val SERVER_URI="tcp://192.168.219.104:1883"
//컴주소
class DeviceControlFragment : Fragment() {

    private var _binding: FragmentDeviceControlBinding? = null
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

        _binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchLedLivingroom.setOnCheckedChangeListener{ compoundButton,b->
            if(b){
                mqttClient.publish(PUB_TOPIC_led_livingroom,"on")
            }
            else{
                mqttClient.publish(PUB_TOPIC_led_livingroom,"off")
            }

        }
        binding.switchLedKitchen.setOnCheckedChangeListener{ compoundButton,b->
            if(b){
                mqttClient.publish(PUB_TOPIC_led_kitchen,"on")
            }
            else{
                mqttClient.publish(PUB_TOPIC_led_kitchen,"off")
            }

        }
        binding.switchLedMainroom.setOnCheckedChangeListener{ compoundButton,b->
            if(b){
                mqttClient.publish(PUB_TOPIC_led_mainroom,"on")
            }
            else{
                mqttClient.publish(PUB_TOPIC_led_mainroom,"off")
            }

        }
        binding.seekBarBlind.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(view: SeekBar?, value: Int, fromUser: Boolean) {
                if(fromUser){
                    binding.blindvalue.text="블라인드 각도: $value"

                    //Seekbar의 값을 publish
                    mqttClient.publish(PUB_TOPIC_blind,"$value")
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        binding.livingroomPwm.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(view: SeekBar?, value: Int, fromUser: Boolean) {
                if(fromUser){
//                    binding.txtAngle.text="카메라 각도: $value"

                    //Seekbar의 값을 publish
                    mqttClient.publish(PUB_TOPIC_led_livingroom,"$value")
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        binding.kitchenPwm.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(view: SeekBar?, value: Int, fromUser: Boolean) {
                if(fromUser){
//                    binding.txtAngle.text="카메라 각도: $value"

                    //Seekbar의 값을 publish
                    mqttClient.publish(PUB_TOPIC_led_kitchen,"$value")
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        binding.mainroomPwm.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(view: SeekBar?, value: Int, fromUser: Boolean) {
                if(fromUser){
//                    binding.txtAngle.text="카메라 각도: $value"

                    //Seekbar의 값을 publish
                    mqttClient.publish(PUB_TOPIC_led_mainroom,"$value")
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })



        binding.autoBlind.setOnCheckedChangeListener{ compoundButton,b->
            if(b){
                mqttClient.publish(PUB_TOPIC_blind,"automode_on")
            }
            else{
                mqttClient.publish(PUB_TOPIC_blind,"automode_off")
            }

        }

        OpenWeather.getweather("seoul"){
            val iconCode=it.weather[0].icon
            val iconUrl="http://openweathermap.org/img/wn/01d@2x.png"
            val weather="서울  ${it.weather[0].description}  온도${it.main.temp} / 습도 ${it.main.humidity}"
            Glide.with(this).load(iconUrl).override(150,150).into(binding.weathericonSeoul)
            binding.txtweatherSeoul.text=weather
            Log.d("currentweather",it.toString())
        }
        OpenWeather.getweather("ansan"){
            val iconCode=it.weather[0].icon
            val iconUrl="http://openweathermap.org/img/wn/01d@2x.png"
            val weather="안산  ${it.weather[0].description}  온도${it.main.temp} / 습도 ${it.main.humidity}"
            Glide.with(this).load(iconUrl).override(150,150).into(binding.weathericonAnsan)
            binding.txtweatherAnsan.text=weather
            Log.d("currentweather",it.toString())
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