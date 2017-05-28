package com.example.nikolajcolic.jazgobar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.DataAll;
import com.example.Goba;
import com.example.Lokacija;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

/**
 * Created by nikolajcolic on 13/03/17.
 */

public class AdapterLokacija extends RecyclerView.Adapter<AdapterLokacija.ViewHolder>{
    DataAll all;
    ActivityZacetna ac;
    Location last;
    ApplicationMy app;
    public static int UPDATE_DISTANCE_IF_DIFF_IN_M=10;


    public void setLastLocation(Location l) {
        if (last==null) {
            last = l;
            notifyDataSetChanged();
        }
        else {
            if (Util.distance(last.getLatitude(),last.getLongitude(),l.getLatitude(),l.getLongitude())>UPDATE_DISTANCE_IF_DIFF_IN_M){
                last = l;
                notifyDataSetChanged();
            }
        }
    }

    public AdapterLokacija(DataAll all, ActivityZacetna ac) {
        super();
        this.all = all;
        this.ac = ac;
        last=ac.getLocation();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    //{ // each data item is just a string in this case
    public TextView txtHeader;

        public TextView txtDis;
        public TextView txtTime;
        public ImageView iv;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);

            txtDis = (TextView) v.findViewById(R.id.textViewDistance);
            txtTime = (TextView) v.findViewById(R.id.textViewTime);
            iv = (ImageView)v.findViewById(R.id.icon);
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Meni");
            menu.add(0, v.getId(), getAdapterPosition(), "Uredi");
            menu.add(0, v.getId(), getAdapterPosition(), "Deli z vsemi");
            menu.add(0, v.getId(), getAdapterPosition(), "Izbriši");
        }
        /*@Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Meni");
            menu.add(0, v.getId(), 0, "Izberi");//groupId, itemId, order, title
            menu.add(0, v.getId(), 0, "Uredi");
            menu.add(0, v.getId(), 0, "Izbriši");
        }*/
    }
    /*public void add(int position,Oglas item) {
        mDataset.dodaj().add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
    */


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    private static void startDView(Lokacija tmp, Activity ac, String id_user) {
        //  System.out.println(name+":"+position);
        //Intent i = new Intent(ac.getBaseContext(), ActivityLocation.class);
        if (tmp.getIdUser().equals(id_user)) {
            Intent i = new Intent(ac.getBaseContext(), ActivityLocation.class);
            i.putExtra(DataAll.LOKACIJA_ID, tmp.getId());
            ac.startActivity(i);
        }else{
            Intent i = new Intent(ac.getBaseContext(), ActivityLocationReadOnly.class);
            i.putExtra(DataAll.LOKACIJA_ID, tmp.getId());
            ac.startActivity(i);
        }

    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Lokacija trenutni = all.getLocation(position);
        //Goba g=all.getLocGoba(trenutni);
        final String name = trenutni.getIme();
        holder.txtHeader.setText(Html.fromHtml(DataAll.getHtmlFormatedLocationTagList(all.getGobaList(trenutni.getId()))));
        holder.itemView.setLongClickable(true);
        //holder.iv.setImageDrawable(this.ac.getDrawable(R.drawable.goba));
        /*File imgFile = new  File(trenutni.getSlika());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.iv.setImageBitmap(myBitmap);
        }else{
            holder.iv.setImageResource(R.drawable.goba);
        }*/
        File imgFile = new  File(trenutni.getSlika());
        if (imgFile.exists()) {
            //"http://i.imgur.com/DvpvklR.png"
            System.out.println("Picasso: "+trenutni.getSlika());
            Picasso.with(ac.getApplicationContext())
                    .load(imgFile) //URL
                    .placeholder(R.drawable.ic_cloud_download_black_124dp)
                    .error(R.drawable.ic_error_black_124dp)
                    // To fit image into imageView
                    .fit()
                    // To prevent fade animation
                    .noFade()
                    .into(holder.iv);

            //   Picasso.with(ac).load(trenutni.getFileName()).into(holder.iv);
            // holder.iv.setImageDrawable(this.ac.getDrawable(R.drawable.ic_airline_seat_recline_extra_black_24dp));
        }
        else {
            holder.iv.setImageResource(R.drawable.goba);
        }
        /*if (position%2==1) {
            holder.txtHeader.setTextColor(Color.BLUE);
        } else {
            holder.txtHeader.setTextColor(Color.RED); //VAJE 7

        }
        holder.txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterLokacija.startDView(trenutni.getId(),ac);
            }
        });
        holder.txtFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterLokacija.startDView(trenutni.getId(),ac);
            }
        });
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterLokacija.startDView(trenutni.getId(),ac);
            }
        });*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterLokacija.startDView(trenutni,ac,"nikolaj.colic@student.um.si");
            }
        });

        if (last==null) holder.txtDis.setText("N/A");
        else  holder.txtDis.setText(Util.getDistanceInString(ac.getLocation().getLatitude(), ac.getLocation().getLongitude(),trenutni.getX(),trenutni.getY()));
        //holder.txtFooter.setText("Footer: " + trenutni.getLatitude()+","+trenutni.getLongitude());
        //holder.txtTime.setText(Util.getTimeDiff(System.currentTimeMillis(), trenutni.getDatum()));
        holder.txtTime.setText(DateFormat.format("dd.MM.yyyy hh:mm:ss", new Date(trenutni.getDatum())).toString());
    }
    public void removeItem(int position) {
        all.getLokacijaAll().remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return all.getLocationSize();
    }

}
