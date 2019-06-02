package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 04/05/2016.
 */

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoPreference extends Preference {
    private TextView mensagem;
    private ImageView icone;
    private View viewDaPreferencia;

    public TextView getMensagem() {
        return mensagem;
    }

    public InfoPreference(Context context) {
        super(context);
    }

    public InfoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.preference_info, parent, false);
        return view;
    }

}


