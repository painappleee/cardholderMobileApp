package com.example.lsb3.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lsb3.R
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.FragmentMainBinding
import com.example.lsb3.ui.adapter.CardAdapter
import com.example.lsb3.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    private var position = -1
    private var cards = ArrayList<Card>()
    private lateinit var adapter: CardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        var showCardFragment = ShowCardFragment()

        adapter = CardAdapter(this, cards) { position ->
            val bundle = Bundle()
            bundle.putSerializable("card", cards[position].copy())
            showCardFragment.arguments = bundle

            startFragment(showCardFragment)
        }
        //cards = appSpecificStorageManager.read()

        // toolbar
        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(binding.mainToolbar)
        activity.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add -> {
                        startFragment(AddEditCardFragment())
                        true
                    }
                    R.id.action_save ->{
                        viewModel.saveToPdf(requireContext(),cards)
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.recView.adapter = adapter
        binding.recView.layoutManager = GridLayoutManager(this.context,2)

        // Привет, это я - твой код, не забывай меня коммитить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Тебе же хуже будет!




        viewModel.cards.observe(requireActivity()) {
                updatedCards ->
            CoroutineScope(Dispatchers.IO).launch{
                cards.clear()
                cards.addAll(updatedCards)

                activity?.runOnUiThread{
                    adapter.notifyDataSetChanged()

                }
            }

        }




        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recView)

        binding.buttonAdd.setOnClickListener{
            startFragment(AddEditCardFragment())

        }



    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 121){
            val addEditCardFragment = AddEditCardFragment()
            position = item.groupId

            val bundle = Bundle()
            bundle.putInt("cardId", cards[position].id)
            addEditCardFragment.arguments = bundle

            startFragment(addEditCardFragment)
        }
        else if (item.itemId == 122){
            viewModel.saveBarCode(requireContext(),cards[item.groupId].shtrBitmap!!)
        }

        return super.onContextItemSelected(item)
    }

    private fun startFragment(fragment: Fragment){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frlayout, fragment)
            .commit()
    }


    inner class SwipeToDeleteCallback(private val adapter: CardAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val idToDelete = cards[position].id

            cards.removeAt(position)
            adapter.notifyItemRemoved(position)

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteCard(idToDelete)
            }

        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return when (viewHolder.bindingAdapterPosition % 2) {
                0 -> ItemTouchHelper.LEFT
                else -> ItemTouchHelper.RIGHT
            }
        }

    }

}