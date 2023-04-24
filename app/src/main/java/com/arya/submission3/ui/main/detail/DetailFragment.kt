package com.arya.submission3.ui.main.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.arya.submission3.R
import com.arya.submission3.data.remote.response.Story
import com.arya.submission3.databinding.FragmentDetailBinding
import com.arya.submission3.utils.Result
import com.arya.submission3.utils.ViewModelFactory
import com.arya.submission3.utils.withDateFormat
import com.bumptech.glide.Glide

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel : DetailViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = DetailFragmentArgs.fromBundle(arguments as Bundle).storyId
        binding.detailPhoto.transitionName = DetailFragmentArgs.fromBundle(arguments as Bundle).ivName
        binding.detailName.transitionName = DetailFragmentArgs.fromBundle(arguments as Bundle).tvName
        detailViewModel.getStory(id, detailViewModel.getUserToken()).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    it.data?.story?.let { story -> showStory(story) }
                    detailViewModel.setLoading(false)
                }

                is Result.Loading -> {
                    detailViewModel.setLoading(true)
                }

                is Result.Error -> {
                    detailViewModel.setLoading(false)
                    detailViewModel.setError(it.message ?: "An error occurred")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showStory(story: Story) {
        context?.let { Glide.with(it).load(story.photoUrl).into(binding.detailPhoto) }
        binding.detailName.text = story.name
        binding.detailDate.text = getString(R.string.dateFormat, story.createdAt!!.withDateFormat())
        binding.detailDescription.text = story.description
        if (story.lat != null && story.lon != null) {
            binding.detailBtn.visibility = View.VISIBLE
            binding.detailBtn.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("geo:${story.lat},${story.lon}")).setPackage("com.google.android.apps.maps")
                )
            }
        }
    }
}