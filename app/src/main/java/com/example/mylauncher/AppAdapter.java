package com.example.mylauncher;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppHolder> implements Filterable {
    private Context context;
    private List<Appobject> appObjects;
    private List<Appobject> allAppObjects;

    public AppAdapter(Context context, List<Appobject> appObjects) {
        this.context = context;
        this.appObjects = appObjects;
        allAppObjects = new ArrayList<>(appObjects);
    }

    @NonNull
    @Override
    public AppHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(context);
        View view = myInflater.inflate(R.layout.app_drawer,parent,false);
        return new AppHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppHolder holder, final int position) {
        holder.name.setText(appObjects.get(position).getName());
        holder.img.setImageDrawable(appObjects.get(position).getImage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchAppIntent = context.getPackageManager().getLaunchIntentForPackage(appObjects.get(position).getPackageName());
                if(launchAppIntent != null)
                    context.startActivity(launchAppIntent) ;
            }
        });
    }

    @Override
    public int getItemCount() {

        return appObjects.size();
    }

    public class AppHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView img;
        TextView name;
        LinearLayout linearLayout;
        public AppHolder(@NonNull View itemView) {
            super(itemView);
            img  = (ImageView)itemView.findViewById(R.id.appIcon);
            name = (TextView)itemView.findViewById(R.id.appLabel);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.appLayout);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem uninstall = menu.add(Menu.NONE,1,1,"Uninstall");
            MenuItem appInfo = menu.add(Menu.NONE,2,2,"App Info");
            uninstall.setOnMenuItemClickListener(onClickMenu);
            appInfo.setOnMenuItemClickListener(onClickMenu);
        }

        private final MenuItem.OnMenuItemClickListener onClickMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int pos  =   getAdapterPosition();
                switch (item.getItemId()){
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:" + appObjects.get(pos).getPackageName()));
                        context.startActivity(intent);
                        appObjects.remove(pos);
                        notifyDataSetChanged();
                        break;
                    case 2:
                        Toast.makeText(context,"App Info",Toast.LENGTH_SHORT).show();
                        try {
                            //Open the specific App Info page:
                            Intent intent1 = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent1.setData(Uri.parse("package:" + appObjects.get(pos).getPackageName()));
                            context.startActivity(intent1);

                        } catch ( ActivityNotFoundException e ) {
                            //Open the generic Apps page:
                            Intent intent1 = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            context.startActivity(intent1);
                        }
                        break;
                }
                return true;
            }
        };

    }
    @Override
    public Filter getFilter() {
        return appFilter;
    }

    private Filter appFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Appobject> filteredApp = new ArrayList<>();
            if(constraint == null || constraint.length()==0)
                filteredApp.addAll(allAppObjects);
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Appobject app:allAppObjects){
                    if(app.getName().toLowerCase().contains(filterPattern)){
                        filteredApp.add(app);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredApp;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            appObjects.clear();
            appObjects.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
