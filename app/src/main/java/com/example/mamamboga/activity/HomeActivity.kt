package com.example.mamamboga.activity

import com.example.mamamboga.model.ItemModel
import com.example.mamamboga.adapter.HomeAdapter
import com.example.mamamboga.model.UserDetailModel
import com.example.mamamboga.utils.Constants.USER_DETAIL
import com.example.mamamboga.utils.SharedPreferenceUtils
import com.example.mamamboga.utils.Utility.getDeviceId
import com.example.mamamboga.utils.Utility.getDeviceManufacturer
import com.example.mamamboga.utils.Utility.getDeviceModel
import com.example.mamamboga.utils.Utility.getOsVersionName
import com.example.mamamboga.R
import com.example.mamamboga.utils.Constants.REQUEST_CODE_UPDATE
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.daimajia.slider.library.Tricks.ViewPagerEx
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), OnSliderClickListener, ViewPagerEx.OnPageChangeListener, HomeAdapter.ClickListener, NavigationView.OnNavigationItemSelectedListener {
    private val itemList: MutableList<ItemModel> = mutableListOf()
    private var recyclerView: RecyclerView? = null
    var adapter: HomeAdapter? = null
    var sliderLayout: SliderLayout? = null
    var hashMapForLocal: HashMap<String, Int> = HashMap()
    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        getUserDetail()
        inAppUpdate()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Fruit and Mboga Menu"
        setSupportActionBar(toolbar)
        sliderLayout = findViewById(R.id.slider)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        recyclerView = findViewById(R.id.recyclerView)

        if (!isOnline()) {
            alertDialog()
        }
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        AddImageUrlLocal()
        // Call below method if you want to stop Automatic sliding
        for (name in hashMapForLocal.keys) {
            val textSliderView = TextSliderView(this@HomeActivity)
            textSliderView.description(name).image(hashMapForLocal[name]!!).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(this)
            textSliderView.bundle(Bundle())
            textSliderView.bundle.putString("extra", name)
            sliderLayout?.addSlider(textSliderView)
        }
        sliderLayout?.setPresetTransformer(SliderLayout.Transformer.DepthPage)
        sliderLayout?.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        sliderLayout?.setCustomAnimation(DescriptionAnimation())
        sliderLayout?.setDuration(3000)
        sliderLayout?.addOnPageChangeListener(this@HomeActivity)
        // Bottom
        prepareItem()
        adapter = HomeAdapter(itemList)
        adapter?.setClickListener(this)
        adapter?.setClickListener(this)
        //RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
        // Try Grid layout for Recycler view... YaY!
        recyclerView?.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
    }

    //  In-app update
    private fun inAppUpdate() {
        val updateManager = AppUpdateManagerFactory.create(this)
        updateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                updateManager.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE)
            }
        }
    }

    //  get user basic Detail
    private fun getUserDetail() {
        if (isOnline()) {
            val reference = FirebaseDatabase.getInstance().getReference(USER_DETAIL)
            val userDetail = UserDetailModel(getOsVersionName(), getDeviceModel(), getDeviceManufacturer())
            reference.child(getDeviceId(this)).setValue(userDetail)
        }
    }

    fun AddImageUrlLocal() {
        hashMapForLocal = HashMap()
        hashMapForLocal["Fruits"] = R.drawable.fruit
        hashMapForLocal["Cauliflower"] = R.drawable.cauliflower
        hashMapForLocal["Roots and Tubers"] = R.drawable.arrowroot
        hashMapForLocal["Waru and Ngwaci"] = R.drawable.potatowaru
    }

    override fun onStop() {
        sliderLayout!!.stopAutoCycle()
        super.onStop()
    }

    override fun onSliderClick(slider: BaseSliderView) {
        // for debug
        // Log.e("silder clicked:", " ${slider.bundle["extra"]}")
        //  Toast.makeText(this,slider.getBundle().get("extra")+ "",Toast.LENGTH_SHORT).show();
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        // for debug
        // Log.d("Slider Demo", "Page Change:$position")
    }

    override fun onPageScrollStateChanged(state: Int) {}
    // end top section
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bottom section
    private fun prepareItem() {
        // var item = Item(R.drawable.shutterstock, "Flower", "Price: KSh. 30")
        // itemList.add(item)
        itemList.add(ItemModel(R.drawable.shutterstock, "Cauliflower & Broccoli", "Price: KSh. 30"))
        itemList.add(ItemModel(R.drawable.apple, "Fruit", "Price: KSh. 150"))
        itemList.add(ItemModel(R.drawable.lettuce2, "Leafy Greens", "Price: KSh. 70"))
        itemList.add(ItemModel(R.drawable.carrot, "Roots & Tubers", "Price: KSh. 30"))
        itemList.add(ItemModel(R.drawable.tomato, "Fresh Salad", "Price: KSh. 20"))
    }

    override fun itemClicked(view: View?, position: Int) {

        when (position) {
            0 -> SharedPreferenceUtils(this).setCategoryItem("flower")
            1 -> SharedPreferenceUtils(this).setCategoryItem("fruit")
            2 -> SharedPreferenceUtils(this).setCategoryItem("leaves")
            3 -> SharedPreferenceUtils(this).setCategoryItem("root")
            4 -> SharedPreferenceUtils(this).setCategoryItem("salad")
            else -> Log.e("position :", position.toString())
        }
        // Go to category activity and pass the selection to the new activity
        startActivity(Intent(this@HomeActivity, CategoryItemActivity::class.java).putExtra("ItemPosition ", position))
    }

    // Navigation Drawer
    override fun onBackPressed() {
//        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                moveTaskToBack(true)
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            //  super.onBackPressed();
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fruit -> navigationItemActivity("fruit")
            R.id.salid -> navigationItemActivity("salad")
            R.id.flower -> navigationItemActivity("flower")
            R.id.leaves -> navigationItemActivity("leaves")
            R.id.root -> navigationItemActivity("root")
            R.id.nav_send -> startActivity(Intent(this@HomeActivity, ContactActivity::class.java))
            R.id.mycart -> startActivity(Intent(this@HomeActivity, MyCartActivity::class.java))
            R.id.logout -> logOutUser()
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigationItemActivity(stSharedPref: String) {
        SharedPreferenceUtils(this).setCategoryItem(stSharedPref)
        startActivity(Intent(this@HomeActivity, CategoryItemActivity::class.java))
    }
    // if the device is not connected to the internet
    private fun alertDialog() {
        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setMessage("Please Connect to Internet")
        builder.setCancelable(false).setIcon(R.drawable.ic_internet).setTitle("No Internet Connection")
        builder.setPositiveButton("OK"
        ) { dialog, id ->
            dialog.cancel()
            finish()
        }.setNegativeButton("Retry") { dialog, which ->
            if (!isOnline()) {
                alertDialog()
            } else {
                dialog.dismiss()
            }
        }
        val alert = builder.create()
        alert.show()
    }
    // if the device is connected to the internet
    private fun isOnline(): Boolean {
        val result: Boolean
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)
                    ?: return false

            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            result = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
//            connectivityManager.run {
//                connectivityManager.activeNetworkInfo ?.run {
//                    result = when(type) {
//                        ConnectivityManager.TYPE_WIFI -> true
//                        ConnectivityManager.TYPE_MOBILE -> true
//                        ConnectivityManager.TYPE_ETHERNET -> true
//                        else -> false
//                    }
//                }
//            }
        }
        return result
    }
    private fun logOutUser(){
        // Shared Prefs
        val sharedPref: SharedPreferences = getSharedPreferences("user-welcome", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.apply {
            putBoolean("user-welcome", false)
        }.apply()
        // sign out firebase user, exit to login screen and clear back stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        FirebaseAuth.getInstance().signOut()
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bucket -> {
                startActivity(Intent(this, CartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}