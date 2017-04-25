package construction.thesquare.shared.start.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;

/**
 * Created by juanmaggi on 12/5/16.
 */
public class TutorialFragmentStep extends Fragment {

    private String title;
    private String text;
    private int imageResource;
    @BindView(R.id.ivFragmentTutorial) ImageView ivTutorial;
    @BindView(R.id.tvFragmentTutorialTitle) TextView tvTitle;
    @BindView(R.id.tvFragmentTutorialText) TextView tvText;

    public static TutorialFragmentStep newInstance(String title, String text, int imageResource) {
        TutorialFragmentStep f =  new TutorialFragmentStep();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        args.putInt("imageResource", imageResource);
        f.setArguments(args);
        return f;
    }

    public TutorialFragmentStep() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);
        ButterKnife.bind(this, rootView);
        title = getArguments().getString("title");
        text = getArguments().getString("text");
        imageResource = getArguments().getInt("imageResource");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText(title);
        tvText.setText(text);
        ivTutorial.setImageResource(imageResource);
    }
}
