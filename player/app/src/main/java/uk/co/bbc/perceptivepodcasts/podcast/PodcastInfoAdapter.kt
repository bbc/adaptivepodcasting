package uk.co.bbc.perceptivepodcasts.podcast

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import uk.co.bbc.perceptivepodcasts.R

class PodcastInfoAdapter(
    private val context: Context,
    private val listDataHeader: List<String>,
    private val listDataChild: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listDataChild[listDataHeader[groupPosition]]?.get(childPosition) ?: ""
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        val childText = getChild(groupPosition, childPosition) as String
        val childView = convertView ?: createNewChildView(parent)
        val txtListChild = childView.findViewById<TextView>(R.id.lblListItem)
        txtListChild.text = childText
        return childView
    }

    private fun createNewChildView(parent: ViewGroup) =
        LayoutInflater.from(context).inflate(R.layout.podcast_info_listrow, parent, false)

    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataChild[listDataHeader[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): String {
        return listDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        val headerTitle = getGroup(groupPosition)
        val groupView = convertView ?: createNewGroupView(parent)
        val lblListHeader = groupView.findViewById<TextView>(R.id.lblListHeader)
        lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.text = headerTitle
        return groupView
    }

    private fun createNewGroupView(parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.podcast_info_listgroup, parent, false)
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}