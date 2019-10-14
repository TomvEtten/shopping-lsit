package hva.nl.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_shopping_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var productRepository: ProductRepository
    private val productList = arrayListOf<Product>()
    private val productAdapter = ProductAdapter(productList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        productRepository = ProductRepository(this)
        initViews()
    }

    private fun initViews() {
        rvProducts.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvProducts.adapter = productAdapter
        rvProducts.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        createItemTouchHelper().attachToRecyclerView(rvProducts)
        fabAddProduct.setOnClickListener { addProduct() }
        getShoppingListFromDatabase()
    }

    private fun addProduct() {
        if (etAddProduct.text.toString() == "" || etQuantity.text.toString() == "") {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            val product = Product(
                quantity = etQuantity.text.toString().toInt(),
                name = etAddProduct.text.toString()
            )
            withContext(Dispatchers.IO) {
                productRepository.insertProduct(product)
            }
        }
        getShoppingListFromDatabase()
    }

    private fun getShoppingListFromDatabase() {
        this.productList.clear()
        CoroutineScope(Dispatchers.Main).launch {
            val allProducts = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }
            this@ShoppingListActivity.productList.addAll(allProducts)
            this@ShoppingListActivity.productAdapter.notifyDataSetChanged()
        }


    }

    private fun createItemTouchHelper(): ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Enables or Disables the ability to move items up and down.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Callback triggered when a user swiped an item.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val productToDelete = productList[position]
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        productRepository.deleteProduct(productToDelete)
                    }
                    getShoppingListFromDatabase()
                }
            }
        }
        return ItemTouchHelper(callback)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete_shopping_list -> {deleteShoppingList() ; true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteShoppingList() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                productRepository.deleteAllProducts()
            }
            getShoppingListFromDatabase()
        }
    }

}
