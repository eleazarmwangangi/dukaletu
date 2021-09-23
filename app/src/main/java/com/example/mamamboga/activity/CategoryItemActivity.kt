package com.example.mamamboga.activity

import com.example.mamamboga.interfaces.ItemClickListener
import com.example.mamamboga.model.FoodDetailModel
import com.example.mamamboga.utils.CustomProgressBar
import com.example.mamamboga.utils.SharedPreferenceUtils
import com.example.mamamboga.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class CategoryItemActivity : AppCompatActivity() {

    var recyclerView: RecyclerView? = null
    var database: FirebaseDatabase? = null
    var allFoodData: DatabaseReference? = null
    var rlNoData: RelativeLayout? = null
    var foodName = ""
    var localFoodName = ""
    var imageUrl = ""
    var foodAdvantage = ""
    var diseaseHeal = ""
    var vitamins = ""
    var precautions = ""
    var price = ""
//    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_item)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView = findViewById(R.id.recyclerView)
        rlNoData = findViewById(R.id.rl_no_data)
        CustomProgressBar.progressBar(this, "Please Wait ...")
//        progressDialog = ProgressDialog(this)
//        progressDialog?.setMessage("Please wait ...")
//        progressDialog?.setCancelable(false)
//        progressDialog?.show()

        rlNoData?.visibility = View.GONE
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (SharedPreferenceUtils(this).getCategoryItem() != "null") {
            rlNoData?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
            database = FirebaseDatabase.getInstance()

            allFoodData = database!!.getReference("AllFood/" + SharedPreferenceUtils(this).getCategoryItem())

            CustomProgressBar.showProgressbar()
        } else {
            rlNoData?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
            CustomProgressBar.dismissProgressBar()
        }
        loadData()
    }

    fun loadData() {
        val options: FirebaseRecyclerOptions<FoodDetailModel> = FirebaseRecyclerOptions.Builder<FoodDetailModel>().setQuery(allFoodData!!, FoodDetailModel::class.java).setLifecycleOwner(this).build()

        val adapter: FirebaseRecyclerAdapter<FoodDetailModel, CategoryItemHolder> = object : FirebaseRecyclerAdapter<FoodDetailModel, CategoryItemHolder>(options) {

            override fun onBindViewHolder(viewHolder: CategoryItemHolder, position: Int, model: FoodDetailModel) {

                Picasso.with(baseContext).load(model.foodImage).placeholder(R.drawable.placeholder).into(viewHolder.ivFood)
                viewHolder.tvName.setText(model.foodName)
                viewHolder.tvLocalName.setText(model.localFoodName)

                CustomProgressBar.dismissProgressBar()
                val clickItem: FoodDetailModel = model
                viewHolder.setItemClickListener(object : ItemClickListener {
                    override fun onClick(view: View?, position: Int, isLongClick: Boolean) {
                        foodName = clickItem.foodName
                        localFoodName = clickItem.localFoodName
                        imageUrl = clickItem.foodImage
                        foodAdvantage = clickItem.advantages
                        vitamins = clickItem.vitamins
                        diseaseHeal = clickItem.diseaseHeal
                        precautions = clickItem.precaution
                        price = clickItem.price
                        val foodNamePrice = "$foodName\n$localFoodName\n$price"

                        val intent = Intent(this@CategoryItemActivity, DetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("id", clickItem.id)
                        bundle.putString("foodName", foodName)
                        bundle.putString("imageurl", imageUrl)
                        bundle.putString("advantage", foodAdvantage)
                        bundle.putString("vitamins", vitamins)
                        bundle.putString("diseaseHeal", diseaseHeal)
                        bundle.putString("precautions", precautions)
                        bundle.putString("price", price)

                        intent.putExtras(bundle)
                        startActivity(intent)
                    }

                })
            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CategoryItemHolder {
                val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.food_layout, viewGroup, false)
                return CategoryItemHolder(view)
            }
        }
        recyclerView!!.adapter = adapter
    }

    class CategoryItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvLocalName: TextView = itemView.findViewById(R.id.tv_local_name)
        var ivFood: ImageView = itemView.findViewById(R.id.iv_food)

        private var itemClickListener: ItemClickListener? = null

        fun setItemClickListener(itemClickListener: ItemClickListener?) {
            this.itemClickListener = itemClickListener
        }

        override fun onClick(v: View) {
            itemClickListener?.onClick(v, adapterPosition, false)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
//        CustomProgressBar.ProgressBar(this, "Please Wait ...")
        /*progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait ...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
        loadData()*/
        val ab = supportActionBar
        if (ab != null) {
            ab.setTitle(SharedPreferenceUtils(this).getCategoryItem()?.substring(0, 1)?.toUpperCase() + SharedPreferenceUtils(this).getCategoryItem()?.substring(1))
            ab.setDisplayHomeAsUpEnabled(true)
        }
    }
}
