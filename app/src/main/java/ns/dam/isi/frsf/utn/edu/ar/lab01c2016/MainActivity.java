package ns.dam.isi.frsf.utn.edu.ar.lab01c2016;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TextWatcher {
    private Spanned fromHtml(String string){
        Spanned result;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(string);
        }
        return result;
    }

    private Double obtenerTasaDeInteres(Integer dias, Integer monto) {
        String tasa = "";
        if(dias < 30) {
            if(monto >= 0 && monto < 5000){
                tasa = getResources().getString(R.string.t0_5000_u30);
            } else if(monto >= 5000 && monto < 100000) {
                tasa = getResources().getString(R.string.t5000_99999_u30);
            } else if(monto >= 100000){
                tasa = getResources().getString(R.string.to99999_u30);
            }
        } else {
            if(monto >= 0 && monto < 5000){
                tasa = getResources().getString(R.string.t0_5000_o30);
            } else if(monto >= 5000 && monto < 100000) {
                tasa = getResources().getString(R.string.t5000_99999_o30);
            } else if(monto >= 100000){
                tasa = getResources().getString(R.string.to99999_o30);
            }
        }

        return Double.parseDouble(tasa);
    }

    private Double caluclarInteres() {
        Double interes = 0.0;
        Integer capital = Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.importeET)).getText()));
        Integer plazo = Integer.parseInt(String.valueOf(((TextView) findViewById(R.id.diasTextView)).getText()));
        Double tasa = this.obtenerTasaDeInteres(plazo, capital);

        interes = (double) capital * (Math.pow((1 + tasa / 100),  ((double) plazo / 360.0)) - 1);

        return (double) Math.round(interes * 100) / 100;
    }

    private void cambiarImporteRendimiento(){
        TextView montoCalculadoTextView = (TextView) findViewById(R.id.montoCalculadoTextView);
        try {
            Double interes = this.caluclarInteres();
            montoCalculadoTextView.setText("$" + interes);
        } catch (Exception e) {
            montoCalculadoTextView.setText("$0.0");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calcularButton = (Button) findViewById(R.id.calcularButton);
        calcularButton.setOnClickListener(this);
        SeekBar seekBar = (SeekBar) findViewById(R.id.diasInversionSeekBar);
        seekBar.setOnSeekBarChangeListener(this);
        EditText et = (EditText) findViewById(R.id.importeET);
        et.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        String texto = "";
        TextView resultadoTextView = (TextView) findViewById(R.id.resultadoTextView);
        EditText emailET = (EditText) findViewById(R.id.emailET);
        EditText cuitET = (EditText) findViewById(R.id.cuitET);
        EditText importeET = (EditText) findViewById(R.id.importeET);
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");

        try {
            if(importeET.getText().toString().isEmpty() ||
                    cuitET.getText().toString().isEmpty() ||
                    emailET.getText().toString().isEmpty() ||
                    !emailPattern.matcher(emailET.getText().toString()).matches()){
                throw new Exception();
            }

            Double interes = this.caluclarInteres();
            texto = String.format(getResources().getString(R.string.successString), interes);
            //texto = String.format("<b>Plazo fijo realizado!</b> Recibirá $%1$.2f al vencimiento.", interes);
            resultadoTextView.setText(this.fromHtml(texto));
            resultadoTextView.setTextColor(ContextCompat.getColor(this, R.color.successColor));
        } catch(Exception e){
            if(importeET.getText().toString().isEmpty()){
                importeET.setError("Este campo no puede estar vacío");
            }
            if(cuitET.getText().toString().isEmpty()){
                cuitET.setError("Este campo no puede estar vacío");
            }
            if(emailET.getText().toString().isEmpty()){
                emailET.setError("Este campo no puede estar vacío");
            } else if (!emailPattern.matcher(emailET.getText().toString()).matches()) {
                emailET.setError("Debes introducir un mail válido");
            }
            texto = getResources().getString(R.string.errorString);
            //texto = "<b>Error!</b> Los campos marcados tienen un error.";
            resultadoTextView.setText(this.fromHtml(TextUtils.htmlEncode(texto)));
            resultadoTextView.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView diasTextView = (TextView) findViewById(R.id.diasTextView);
        diasTextView.setText((new Integer(progress)).toString());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.cambiarImporteRendimiento();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        this.cambiarImporteRendimiento();
    }
}
