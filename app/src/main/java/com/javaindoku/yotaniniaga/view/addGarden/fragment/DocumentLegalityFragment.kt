package com.javaindoku.yotaniniaga.view.addGarden.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.iceteck.silicompressorr.SiliCompressor
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentDocumentLegalityBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentType
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.view.addGarden.AddGardenActivity
import com.javaindoku.yotaniniaga.view.addGarden.adapter.DocumentListAdapter
import com.javaindoku.yotaniniaga.view.addGarden.listener.DocumentListChangedListener
import com.javaindoku.yotaniniaga.view.delivery.dialog.AddDocumentDialog
import com.javaindoku.yotaniniaga.viewmodel.AddGardenViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class DocumentLegalityFragment : BaseFragment(), DocumentListAdapter.ActionListener,
    DocumentListChangedListener {
    val REQUEST_CODE = 90
    private var photoOrGallery: Int = 0
    private lateinit var imageUriData: Uri
    private lateinit var valuesData: ContentValues
    private lateinit var binding: FragmentDocumentLegalityBinding
    private var PERMISSION_REQUEST_CODE = 132
    @Inject
    lateinit var viewModel: AddGardenViewModel
    lateinit var parent: AddGardenActivity
    lateinit var documentDialog: AddDocumentDialog
    private var documentTypeList = ArrayList<DocumentType>().toMutableList()
    private lateinit var documentTypeAdapter: CustomSpinnerAdapter<DocumentType>
    private lateinit var onNext: View.OnClickListener
    private lateinit var documentListAdapter:DocumentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_document_legality, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as AddGardenActivity
        parent.setDocumentDataChangeNotifier(this)
        editMode()
        binding.btnAddDoc.setOnClickListener {
            documentDialog = AddDocumentDialog(
                requireActivity(),
                cancelable = true,
                title = "",
                message = getString(R.string.srConfirmationRegister),
                documentTypeList = documentTypeList,
                documentTypeAdapter = documentTypeAdapter,
                labelPositiveButton = getString(R.string.srTxtBtnPositive),
                labelNegativeButton = getString(R.string.srTxtBtnNegative),
                positiveAction = {document -> submitDocument(document)},
                negativeAction = {},
                document = DocumentGarden(),
                permissionRequestCode = REQUEST_CODE,
                showPictureDialog = {
                    if (checkPersmission()) {
                        valuesData = ContentValues()
                        valuesData.put(MediaStore.Images.Media.TITLE, "New Picture")
                        valuesData.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                        imageUriData = requireContext().contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valuesData
                        )!!
                        showPictureDialog()
                    } else {
                        requestPermission()
                    }
                },
            )
            documentDialog.show()
        }
        parent.loadCertificateDocument()
        getDocumentType()
        binding.btnConfirmationPositive.setOnClickListener(View.OnClickListener {
            onNext.onClick(it)
        })
        binding.btnConfirmationNegative.setOnClickListener { onNext.onClick(it) }
    }

    private fun editMode() {
        if (parent.editMode == "false"){
            binding.btnAddDoc.isEnabled = false
            binding.btnAddDoc.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
        }
    }

    private fun submitDocument(document: DocumentGarden) {
        parent.delegateDocumentList().add(document)
        documentListAdapter.refreshList(parent.delegateDocumentList())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onNext = context as View.OnClickListener
    }



    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_title_bar, null)
        val txvLableTitle = view.findViewById<TextView>(R.id.txvLableTitle)
        txvLableTitle.setText(getString(R.string.srTitleDialogCamera))
        pictureDialog.setCustomTitle(view)
        val pictureDialogItems = arrayOf(
            getString(R.string.choose_Pict_gallery),
            getString(R.string.choose_Pict_photo)
        )
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> pickImageFromGallery()
                1 -> takePicture()
            }
        }
        pictureDialog.show()
    }

    private fun getDocumentType() {
        viewModel.getDocumentType()
        viewModel.documentTypeResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                parent.delegateLoader().show()
            } else if (it.status == ApiResult.Status.ERROR) {
                parent.delegateLoader().dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                parent.delegateLoader().dismiss()
                val documentTypes = it.data!!.data!!
                documentTypeList = documentTypes.toMutableList()
                documentTypeAdapter = CustomSpinnerAdapter<DocumentType>(
                    requireActivity(),
                    documentTypes
                )
            }
        })
    }

    private fun pickImageFromGallery() {
        photoOrGallery = 2
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun takePicture() {
        photoOrGallery = 1
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriData)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE -> {
                    when (photoOrGallery) {
                        1 -> {
                            var resultSuratPicture: String = StringFormat.getRealPathFromURI(imageUriData, requireActivity())
                            var imageBitmap: Bitmap =
                                SiliCompressor.with(requireContext()).getCompressBitmap(resultSuratPicture)
                            documentDialog.setImageDocument(resultSuratPicture, encodeImage(imageBitmap))
                        }
                        2 -> {
                            var dataUri = data!!.data!!
                            var resultSuratPicture: String = StringFormat.getRealPathFromURI(dataUri, requireActivity())
                            var imageBitmap: Bitmap =
                                SiliCompressor.with(requireContext()).getCompressBitmap(resultSuratPicture)
                            documentDialog.setImageDocument(resultSuratPicture, encodeImage(imageBitmap))
                        }
                    }
                }
                else ->
                    Toast.makeText(requireContext(), R.string.srProfileFailedPicture, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun encodeImage(bm: Bitmap): String {
        var baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    override fun onEditClick(documentGarden: DocumentGarden) {
        documentDialog = AddDocumentDialog(
            requireActivity(),
            cancelable = true,
            title = "",
            message = getString(R.string.srConfirmationRegister),
            documentTypeList = documentTypeList,
            documentTypeAdapter = documentTypeAdapter,
            labelPositiveButton = getString(R.string.srTxtBtnPositive),
            labelNegativeButton = getString(R.string.srTxtBtnNegative),
            positiveAction = {document -> submitDocument(document)},
            negativeAction = {},
            document = documentGarden,
            permissionRequestCode = REQUEST_CODE,
            showPictureDialog = {showPictureDialog()},
        )
        documentDialog.show()
    }

    override fun onDeleteClick(position: Int) {
        CustomDialog.customDialogTwoButton(
            requireActivity(),
            true,
            getString(R.string.srConfirmation),
            getString(R.string.srConfirmationMessage),
            getString(R.string.yes),
            getString(R.string.no),
            {activity ->
                parent.delegateDocumentList().removeAt(position)
                documentListAdapter.refreshList(parent.delegateDocumentList())
            },
            {activity ->  },
            null,
            isNegative = false
        ).show()
    }

    override fun onClick(documentGarden: DocumentGarden) {
    }

    private fun bindview(documentList: List<DocumentGarden>) {
        binding.rcyListDocument.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        documentListAdapter = DocumentListAdapter(requireContext(), ArrayList(), this, parent.editMode)
        binding.rcyListDocument.adapter = documentListAdapter
        documentListAdapter.refreshList(documentList)
    }

    override fun onDataChanged(documentList: List<DocumentGarden>) {
        bindview(documentList)
    }

    override fun onResume() {
        super.onResume()
        getDocumentType()
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
//                    showPictureDialog(requestCode)
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }
}