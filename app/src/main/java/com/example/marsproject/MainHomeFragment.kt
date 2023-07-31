package com.example.marsproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marsproject.databinding.FragmentMainHomeBinding
import org.json.JSONObject
import java.net.UnknownServiceException

class MainHomeFragment : Fragment() {
    private lateinit var binding: FragmentMainHomeBinding
    private lateinit var savedname: String
    private lateinit var name: String
    private lateinit var id: String
    private lateinit var objective: String
    private lateinit var title: String
    private lateinit var profile: String
    private var life: Int = 0
    private lateinit var money: String
    private lateinit var level: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainHomeBinding.inflate(inflater)

        // 닉네임 정보 불러오기
        savedname = (activity as MainActivity).getName()

        val HomeThread = Thread {
            try {
                val jsonObject = Request().reqget("http://dmumars.kro.kr/api/getuserdata/${savedname}") //get요청

                name = jsonObject.getJSONArray("results").getJSONObject(0).getString("user_name")
                id = jsonObject.getJSONArray("results").getJSONObject(0).getString("user_id")
                objective = jsonObject.getJSONArray("results").getJSONObject(0).getString("choice_mark")
                title = jsonObject.getJSONArray("results").getJSONObject(0).getString("user_title")
                profile = jsonObject.getJSONArray("results").getJSONObject(0).getString("profile_local")
                life = jsonObject.getJSONArray("results").getJSONObject(0).getInt("life")
                money = jsonObject.getJSONArray("results").getJSONObject(0).getInt("money").toString()
                level = jsonObject.getJSONArray("results").getJSONObject(0).getInt("level").toString()
                // /getdetailmark 부분 파싱 results에서 JSONArray 뽑고 JSONArray[0] 에 mark_id = 3
            } catch (e: UnknownServiceException) {
                // API 사용법에 나와있는 모든 오류응답은 여기서 처리
                println(e.message)
                // 이미 reqget() 메소드에서 파싱 했기에 json 형태가 아닌 value 만 저장 된 상태 만약 {err: "type_err"} 인데 e.getMessage() 는 type_err만 반환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        HomeThread.start()
        HomeThread.join()

        binding.titleName.text = title
        binding.userName.text = name
        binding.userId.text = "@${id}"
        binding.objectiveName.text = objective
        when(life) {
            3 -> {
                binding.lifeImage1.visibility = View.VISIBLE
                binding.lifeImage2.visibility = View.VISIBLE
                binding.lifeImage3.visibility = View.VISIBLE
            }
            2 -> {
                binding.lifeImage1.visibility = View.VISIBLE
                binding.lifeImage2.visibility = View.VISIBLE
                binding.lifeImage3.visibility = View.INVISIBLE
            }
            1 -> {
                binding.lifeImage1.visibility = View.VISIBLE
                binding.lifeImage2.visibility = View.INVISIBLE
                binding.lifeImage3.visibility = View.INVISIBLE
            }
        }

        // 클릭 시 내 주변 사람 찾기로 이동 리스너
        binding.bluetoothImage.setOnClickListener {
            activity?.let{
                val intent = Intent(context, SearchPeopleActivity::class.java)
                startActivity(intent)
            }
        }

        // 클릭 시 목표 탭으로 이동 리스너
        binding.objectiveName.setOnClickListener{
            (activity as MainActivity).clickchangeFragment(1)
        }
        binding.progressBar.setOnClickListener{
            (activity as MainActivity).clickchangeFragment(1)
        }

        return binding.root
    }
}