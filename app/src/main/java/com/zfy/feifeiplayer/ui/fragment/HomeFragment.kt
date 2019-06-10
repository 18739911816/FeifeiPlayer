package com.zfy.feifeiplayer.ui.fragment

import android.graphics.Color
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zfy.feifeiplayer.R
import com.zfy.feifeiplayer.adapter.HomeAdapter
import com.zfy.feifeiplayer.base.BaseFragment
import com.zfy.feifeiplayer.bean.HomeBean
import com.zfy.feifeiplayer.bean.X
import com.zfy.feifeiplayer.util.ThreadUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Runnable
import okhttp3.*
import org.jetbrains.anko.debug
import org.jetbrains.anko.support.v4.runOnUiThread
import java.io.IOException


/**
 *
 */
class HomeFragment : BaseFragment() {

    val pageSize = 10
    var pageNum = 1
    var homeAdapter = HomeAdapter()
    var isloadmore = false

    override fun initView(): View {

        return View.inflate(context, R.layout.fragment_home, null)
    }

    override fun init() {
        super.init()

    }

    override fun initListener() {
        super.initListener()
        rcv.layoutManager = LinearLayoutManager(context)
        rcv.adapter = homeAdapter
        swr.setColorSchemeColors(Color.RED, Color.BLUE, Color.WHITE)

        swr.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            pageNum = 1
            isloadmore = false
            getdata()
        })

        rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val layoutManager = rcv.layoutManager
                        if (layoutManager is LinearLayoutManager) {
                            if (layoutManager.findLastVisibleItemPosition() == homeAdapter.list.size) {
                                isloadmore = true
                                getdata()
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        })

    }

    override fun initData() {
        super.initData()
        getdata()
    }

    private fun getdata() {
//        val homeUrl = URLProviderUtils.getHomeUrl(0, 20)
        val homeUrl = "https://testapi.hngxsj.com/scsj-order/appShopOrder/getShopOrderListByTypeForBuyer"

        val okHttpClient = OkHttpClient()
        val recontent = "{\"pageInfo\":{\"pageNum\":$pageNum,\"pageSize\":$pageSize},\"type\":\"0\"}"

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), recontent)
        val token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNTAzNjE4Nzk2NSIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTU1OTcyODMzNDAyOCwiZXhwIjoxNTYwMzMzMTM0fQ.2Wh15sscSRe2SmZTAMf_XQtPTtr_ZibstXNWkJSkvl7ARs8y4wweVe607_HkQsc3FwJqJLEhOJsminHKjAaDvw"
        val request = Request.Builder()
                .url(homeUrl)
                .addHeader("Authorization", token)
                .post(requestBody)
//                .get()
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        swr?.let { it.isRefreshing = false }
                    }
                })

                debug { "网络请求失败==》" + e.message }
                Log.e("zz", "网络请求失败=2=》" + e.message)
            }

            override fun onResponse(call: Call, response: Response) {

                val result = response.body()?.string()
                Log.e("zz", "网络请求成功=2=》" + result)
                val homeBean = Gson().fromJson<HomeBean>(result, object : TypeToken<HomeBean>() {}.type)
                runOnUiThread {
                    //                    homeAdapter = HomeAdapter(context, homeBean.data.list as ArrayList<X>)
                    swr?.let { it.isRefreshing = false }
                    homeAdapter.updata(homeBean.data.list as ArrayList<X>)
                }

            }

        })
    }

    private fun loadmore(pageNum: Int) {
//        val homeUrl = URLProviderUtils.getHomeUrl(0, 20)
        val homeUrl = "https://testapi.hngxsj.com/scsj-order/appShopOrder/getShopOrderListByTypeForBuyer"

        val okHttpClient = OkHttpClient()

        val recontent = "{\"pageInfo\":{\"pageNum\":$pageNum,\"pageSize\":$pageSize},\"type\":\"0\"}"

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), recontent)
        val token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNTAzNjE4Nzk2NSIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTU1OTcyODMzNDAyOCwiZXhwIjoxNTYwMzMzMTM0fQ.2Wh15sscSRe2SmZTAMf_XQtPTtr_ZibstXNWkJSkvl7ARs8y4wweVe607_HkQsc3FwJqJLEhOJsminHKjAaDvw"
        val request = Request.Builder()
                .url(homeUrl)
                .addHeader("Authorization", token)
                .post(requestBody)
//                .get()
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        swr?.let { it.isRefreshing = false }
                    }
                })

                debug { "网络请求失败==》" + e.message }
                Log.e("zz", "网络请求失败=2=》" + e.message)
            }

            override fun onResponse(call: Call, response: Response) {

                val result = response.body()?.string()
                Log.e("zz", "网络请求成功=2=》" + result)
                val homeBean = Gson().fromJson<HomeBean>(result, object : TypeToken<HomeBean>() {}.type)
                runOnUiThread {

                    swr?.let { it.isRefreshing = false }
                    if (!isloadmore) {
                        homeAdapter.updata(homeBean.data.list as ArrayList<X>)
                    }else{
                        homeAdapter.loadMore(homeBean.data.list as ArrayList<X>)
                    }
                }

            }

        })

    }
}