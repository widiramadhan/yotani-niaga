package com.javaindoku.yotaniniaga.view.transaction.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentTransactionSupplyDemandBinding
import com.javaindoku.yotaniniaga.model.transaction.SupplyDemand
import com.javaindoku.yotaniniaga.view.transaction.adapter.SupplyDemandAdapter

class TransactionSupplyDemandFragment : BaseFragment() {
    private lateinit var binding: FragmentTransactionSupplyDemandBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_transaction_supply_demand, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcyTransactionSupplyDemand.layoutManager = LinearLayoutManager(requireContext())
        val listSupplyDemand = listOf(
            SupplyDemand(id = "1",
            date = "2020-09-13 03:20:15 +07.00",
            invoiceNo = "INV/20200804/XX/VIII/5671237791",
            name = "Suparman",
            supply = "10 Ton",
            image = ""),
            SupplyDemand(id = "2",
                date = "2020-09-14 03:20:15 +07.00",
                invoiceNo = "INV/20200804/XX/VIII/5671237792",
                name = "Superman",
                supply = "11 Ton",
                image = "")
        )
        val adapter = SupplyDemandAdapter(listSupplyDemand)
        binding.rcyTransactionSupplyDemand.adapter = adapter
        binding.rcyTransactionSupplyDemand.addItemDecoration(
            DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL)
        )
    }
}