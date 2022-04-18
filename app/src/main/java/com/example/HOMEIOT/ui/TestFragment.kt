package com.example.HOMEIOT.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebViewClient
import com.example.HOMEIOT.databinding.FragmentTestBinding


class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!  //뷰바인딩

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTestBinding.inflate(inflater, container, false)
       return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.WebView.apply{
            settings.javaScriptEnabled=true
            webViewClient= WebViewClient()
            loadUrl("http://www.google.com")
        }
        binding.txturl.setOnEditorActionListener{_,actionId,_ ->
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                var url=binding.txturl.text.toString()

                if(!url.startsWith("http://")&&!url.startsWith("https://"))
                {
                    url="https://$url"
                    binding.txturl.setText(url)
                }
                binding.WebView.loadUrl(url)
                true
            } else{
                false
            }

        }

    }


}