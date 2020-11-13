package com.example.nearshoe_java


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class pickup2 : AppCompatActivity() {


    lateinit var mAuth: FirebaseAuth
    lateinit var  option : Spinner
    lateinit var txtSelectDate : EditText
    lateinit var txtSelectTime : EditText
    lateinit var txtDeliverySelectDate : EditText
    lateinit var txtDeliverySelectTime : EditText
    lateinit var totalFee : EditText
    lateinit var currentAddress : EditText
    lateinit var deliveryAddress : EditText
    lateinit var btlocation : ImageButton
    lateinit var btSelectDate : Button
    lateinit var btSelectTime : Button
    lateinit var btDelSelectDate : Button
    lateinit var btDelSelectTime : Button
    lateinit var btCompleteOrder : Button
    lateinit var btDeliveryLocation :ImageButton

    private val PLACE_PICKER_REQUEST = 999

    private val PLACE_DELIVERY_REQUEST = 888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickup2)

        option= findViewById<View>(R.id.spinner2) as Spinner
        totalFee=findViewById<View>(R.id.totalFee) as EditText
        currentAddress =findViewById<View>(R.id.pickupLocation) as EditText
        deliveryAddress =findViewById<View>(R.id.deliveryLocation) as EditText
        btlocation =findViewById<View>(R.id.btPickLocation) as ImageButton
        btSelectDate =findViewById<View>(R.id.btSelectDate) as Button
        btSelectTime =findViewById<View>(R.id.btSelectTime) as Button
        txtSelectDate =findViewById<View>(R.id.txtSelectDate) as EditText
        txtSelectTime =findViewById<View>(R.id.txtSelectTime) as EditText
        txtDeliverySelectDate=findViewById<View>(R.id.txtDeliveryDate) as EditText
        txtDeliverySelectTime=findViewById<View>(R.id.txtDeliveryTime) as EditText
        btDelSelectDate=findViewById<View>(R.id.btDeliveryDate) as Button
        btDelSelectTime=findViewById<View>(R.id.btDeliveryTime) as Button
        btCompleteOrder=  findViewById<View>(R.id.btCompletePickup) as Button
        btDeliveryLocation= findViewById<View>(R.id.btDeliveryLocation)as ImageButton

        mAuth = FirebaseAuth.getInstance();
        val c = Calendar.getInstance()
        val year =c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day =c.get(Calendar.DAY_OF_MONTH)
        var amount = 0
        txtSelectDate.isEnabled=false
        txtSelectTime.isEnabled=false
        totalFee.isEnabled=false

        val options = arrayOf("1", "2", "3", "4", "5")

        option.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        option.onItemSelectedListener =object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val mCurrencyFormat = NumberFormat.getCurrencyInstance()
                val myFormatPrice = mCurrencyFormat.format(options.get(p2).toDouble() * 15)
                amount =options.get(p2).toInt()
                totalFee.setText(myFormatPrice)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

                totalFee.setText("please select one")
            }

        }
        btlocation.setOnClickListener {
            openPlacePicker()
        }
        btDeliveryLocation.setOnClickListener {
            openPlacePicker2()
        }


        btCompleteOrder.setOnClickListener {

            if (validate()){
                var map = mutableMapOf<String, Any>()
                map["userId"]=mAuth.currentUser!!.uid
                map["sneakerAmount"]=amount
                map["address"]=currentAddress.text.toString().trim()
                map["Delivery address"]=deliveryAddress.text.toString().trim()
                map["pickupDate"]=txtSelectDate.text.toString().trim()
                map["pickupTime"]=txtSelectTime.text.toString().trim()
                map["deliveryDate"]=txtDeliverySelectDate.text.toString().trim()
                map["deliveryTime"]=txtDeliverySelectTime.text.toString().trim()
                map["totalFee"]= totalFee.text.toString().trim()


                FirebaseDatabase.getInstance().reference
                        .child("PickupAndDelivery").push()
                        .setValue(map).addOnCompleteListener(object : OnCompleteListener<Void> {
                            override fun onComplete(task: Task<Void>) {
                                if (task.isSuccessful) {
                                    Toast.makeText(applicationContext, "Order Successfully", Toast.LENGTH_LONG).show()
                                    val intent = Intent(applicationContext, CustomerDashboard::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    intent.putExtra("EXIT", true)
                                    startActivity(intent)
                                } else {
                                    //display a failure message
                                    Toast.makeText(applicationContext, "Something wrong pls call our customer service", Toast.LENGTH_LONG).show()


                                }

                            }

                        })


            }



        }

        btSelectDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                //set text view
                txtSelectDate.setText("" + mDay + "-" + mMonth + "-" + mYear)
            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }

        btDelSelectDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                //set text view
                txtDeliverySelectDate.setText("" + mDay + "-" + mMonth + "-" + mYear)
            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }
        btSelectTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                //set time to textView
                txtSelectTime.setText(SimpleDateFormat("HH:mm").format(c.time))
            }
            TimePickerDialog(this, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        btDelSelectTime.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                //set time to textView
                txtDeliverySelectTime.setText(SimpleDateFormat("HH:mm").format(c.time))
            }
            TimePickerDialog(this, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

    }



    private fun validate():Boolean{




        if (txtSelectDate.text.toString().isEmpty()){
            txtSelectDate.error="Please select a pickup date"
            return false
        }
        if (txtSelectTime.text.toString().isEmpty()){
            txtSelectTime.error="Please select a pickup time"
            return false
        }
        if (txtDeliverySelectTime.text.toString().isEmpty()){
            txtDeliverySelectTime.error="Please select a delivery time"
            return false
        }
        if (txtDeliverySelectDate.text.toString().isEmpty()){
            txtDeliverySelectDate.error="Please select a delivery Date"
            return false
        }
        if(currentAddress.text.toString().isEmpty()){
            currentAddress.error = "Please enter select pickup address"
            return false
        }
        if(deliveryAddress.text.toString().isEmpty()){
            deliveryAddress.error = "Please enter select delivery address"
            return false
        }
        return true
    }
    private fun openPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)

        } catch (e: GooglePlayServicesRepairableException) {
            Log.d("Exception", e.message!!)
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.d("Exception", e.message!!)
            e.printStackTrace()
        }
    }

    private fun openPlacePicker2() {
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(this), PLACE_DELIVERY_REQUEST)

        } catch (e: GooglePlayServicesRepairableException) {
            Log.d("Exception", e.message!!)
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.d("Exception", e.message!!)
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PLACE_PICKER_REQUEST -> {
                    val place = PlacePicker.getPlace(this, data)
                    val latitude = place.latLng.latitude
                    val longitude = place.latLng.longitude


                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(this, Locale.getDefault())

                    addresses = geocoder.getFromLocation(latitude, longitude, 1)

                    val address = addresses[0].getAddressLine(0)
                    currentAddress.setText(address)
                }
                PLACE_DELIVERY_REQUEST -> {
                    val place1 = PlacePicker.getPlace(this, data)
                    val latitude1 = place1.latLng.latitude
                    val longitude1 = place1.latLng.longitude


                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(this, Locale.getDefault())

                    addresses = geocoder.getFromLocation(latitude1, longitude1, 1)

                    val address1 = addresses[0].getAddressLine(0)
                    deliveryAddress.setText(address1)
                }
            }
        }
    }


}