package com.cyandev.androidproficiencyexercise.module.entity;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyandev.androidproficiencyexercise.R;
import com.cyandev.androidproficiencyexercise.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyandev on 2016/11/25.
 */
public class EntityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PLAIN_TEXT = 1;
    private static final int TYPE_TEXT_WITH_IMAGES = 2;
    private static final int TYPE_MORE_LOADER = 3;

    private List<Entity> entities;
    private boolean isReserving;

    private OnEntityItemClickListener listener;

    public EntityAdapter() {
        entities = new ArrayList<>();
    }

    public void setReserving(boolean reserving) {
        if (this.isReserving == reserving) {
            return;
        }

        this.isReserving = reserving;

        if (reserving) {
            notifyItemInserted(getItemCount());
        } else {
            notifyItemRemoved(getItemCount() - 1);
        }
    }

    public boolean isReserving() {
        return isReserving;
    }

    public void setOnEntityItemClickListener(OnEntityItemClickListener listener) {
        this.listener = listener;
    }

    public OnEntityItemClickListener getOnEntityItemClickListener() {
        return listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_PLAIN_TEXT:
                return EntityViewHolder.create(parent);
            case TYPE_TEXT_WITH_IMAGES:
                return ImageEntityViewHolder.create(parent);
            case TYPE_MORE_LOADER:
                return MoreLoaderViewHolder.create(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int type = getItemViewType(position);

        if (type != TYPE_MORE_LOADER) {
            Entity entity = entities.get(position);
            final EntityViewHolder vh = (EntityViewHolder) holder;
            vh.title.setText(entity.title);
            if (entity.author != null) {
                vh.author.setText(String.format("via %s", entity.author));
            } else {
                vh.author.setText("");
            }
            vh.publishAt.setText(entity.getFormattedPublishDate());
            vh.clickable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(vh.itemView, position);
                    }
                }
            });

            if (type == TYPE_TEXT_WITH_IMAGES) {
                ImageEntityViewHolder imageVH = (ImageEntityViewHolder) vh;
                ImagePagerAdapter adapter = (ImagePagerAdapter) imageVH.pager.getAdapter();
                if (adapter == null) {
                    adapter = new ImagePagerAdapter(imageVH.pager);
                    imageVH.pager.setAdapter(adapter);
                }
                adapter.setUrls(entity.images);
            }
        }
    }

    @Override
    public int getItemCount() {
        return entities.size() + (isReserving ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (isReserving && position == getItemCount() - 1) {
            return TYPE_MORE_LOADER;
        }

        Entity entity = entities.get(position);
        if (entity.images == null || entity.images.size() == 0) {
            return TYPE_PLAIN_TEXT;
        } else {
            return TYPE_TEXT_WITH_IMAGES;
        }
    }

    public void addEntities(List<Entity> entities) {
        int insertionPos = this.entities.size();
        this.entities.addAll(entities);
        notifyItemRangeInserted(insertionPos, entities.size());
    }

    public void clearEntities() {
        int removalCount = entities.size();
        entities.clear();
        notifyItemRangeRemoved(0, removalCount);
    }

    public List<Entity> getEntities() {
        return entities;
    }


    public interface OnEntityItemClickListener {
        void onClick(View view, int position);
    }

}

class EntityViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView author;
    public TextView publishAt;
    public View clickable;

    public static EntityViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entity_plain, parent, false);
        return new EntityViewHolder(view);
    }

    public EntityViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.text);
        author = (TextView) itemView.findViewById(R.id.text_author);
        publishAt = (TextView) itemView.findViewById(R.id.text_publish_at);
        clickable = itemView.findViewById(R.id.clickable);
    }

}

class ImageEntityViewHolder extends EntityViewHolder {

    public ViewPager pager;

    public static ImageEntityViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entity_image, parent, false);
        return new ImageEntityViewHolder(view);
    }

    public ImageEntityViewHolder(View itemView) {
        super(itemView);

        pager = (ViewPager) itemView.findViewById(R.id.pager);
    }

}

class MoreLoaderViewHolder extends RecyclerView.ViewHolder {

    public static MoreLoaderViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_more_loader, parent, false);
        return new MoreLoaderViewHolder(view);
    }

    public MoreLoaderViewHolder(View itemView) {
        super(itemView);
    }

}