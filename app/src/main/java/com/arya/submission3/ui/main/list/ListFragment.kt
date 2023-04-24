package com.arya.submission3.ui.main.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arya.submission3.R
import com.arya.submission3.databinding.FragmentListBinding
import com.arya.submission3.ui.main.list.ListFragmentDirections.actionListFragmentToDetailFragment
import com.arya.submission3.utils.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListAdapter

    private val listViewModel : ListViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvListMain.layoutManager = LinearLayoutManager(activity)

        binding.rvListMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fab.extend()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    binding.fab.shrink()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        showStories()
    }

    override fun onResume() {
        super.onResume()
        listViewModel.setLoading(true)
        adapter.refresh()
        lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    delay(300)
                    binding.rvListMain.smoothScrollToPosition(0)
                    listViewModel.setLoading(false)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showStories() {
        listViewModel.setLoading(true)
        adapter = ListAdapter { it, iv, tv ->
            val toDetail = actionListFragmentToDetailFragment(it.id, iv.transitionName, tv.transitionName)
            findNavController().navigate(toDetail, FragmentNavigator.Extras.Builder()
                .addSharedElement(iv, iv.transitionName)
                .addSharedElement(tv, tv.transitionName)
                .build()
            )
        }
        binding.rvListMain.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        listViewModel.stories.observe(viewLifecycleOwner) {
            listViewModel.setLoading(false)
            adapter.submitData(lifecycle, it)
        }
    }
}