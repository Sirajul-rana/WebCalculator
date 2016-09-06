package hr.web.test;

import hr.web.test.R;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class WebAppInterface {
	WebActivity wa=null;
	String firstinput="0";
    String secondinput="0";
    String ope;
	String result;
    boolean opebool=true;
    int flag=0;
	Context c;
    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        wa=(WebActivity)c;
    }
    public void showToast(String toast) {
    	Log.d("test","event fired");
    	wa.button.setText("new text");
        //Toast.makeText(wa, toast, Toast.LENGTH_LONG).show();
    }
    public void reload(){
    	wa.mHandler.post(new Runnable() {
			public void run() {
				WebAppInterface.this.wa.myWebView.reload();
				Log.d("try", "reload");
			}
		});
	}
    public void addNum(String num){

        if(flag==0){
            firstinput = firstinput+""+num;
            Log.d("Web", firstinput);
        }
        else if (flag==1){
            secondinput = secondinput+""+num;
            Log.d("Web", secondinput);
        }
    }

    public void addOperator(String operator){
        flag  = 1;
        if (opebool==true){
            ope = operator;
            opebool=false;
        }
        else if (opebool==false){
            ope = operator;
        }

        Log.d("Web", firstinput);
    }

    public void allClear(){
        flag = 0;
        firstinput="0";
        secondinput="0";
        Log.d("Web", "clear");
    }

    public String getResult(){
        String combine=firstinput+ope+secondinput;
        flag = 0;

    	result = calculate(combine)+"";
        firstinput=result;

        Log.d("Web", result);
    	return result;

    }
    
    
    public void testMethod(){
    	Log.d("changed event","success");
    	//wa.button.setText("changed name");
    	/*wa.mHandler.post(new Runnable() {
			public void run() {
				Button btn= (Button)WebAppInterface.this.wa.findViewById(R.id.button1);
				btn.setText("new Changed");
			}
		});*/
    	wa.runOnUiThread(new Runnable() {
			public void run() {
				Button btn= (Button)WebAppInterface.this.wa.findViewById(R.id.button1);
				btn.setText("Changed");
			}
		});
	}

    public static double calculate(final String str) {
        class Parser {
            int pos = -1, c;

            void eatChar() {
                c = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            void eatSpace() {
                while (Character.isWhitespace(c)) eatChar();
            }

            double parse() {
                eatChar();
                double v = parseExpression();
                if (c != -1) throw new RuntimeException("Unexpected: " + (char)c);
                return v;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor | term brackets
            // factor = brackets | number | factor `^` factor
            // brackets = `(` expression `)`

            double parseExpression() {
                double v = parseTerm();
                for (;;) {
                    eatSpace();
                    if (c == '+') { // addition
                        eatChar();
                        v += parseTerm();
                    } else if (c == '-') { // subtraction
                        eatChar();
                        v -= parseTerm();
                    } else {
                        return v;
                    }
                }
            }

            double parseTerm() {
                double v = parseFactor();
                for (;;) {
                    eatSpace();
                    if (c == '/') { // division
                        eatChar();
                        v /= parseFactor();
                    } else if (c == '*' || c == '(') { // multiplication
                        if (c == '*') eatChar();
                        v *= parseFactor();
                    } else {
                        return v;
                    }
                }
            }

            double parseFactor() {
                double v;
                boolean negate = false;
                eatSpace();
                if (c == '+' || c == '-') { // unary plus & minus
                    negate = c == '-';
                    eatChar();
                    eatSpace();
                }
                if (c == '(') { // brackets
                    eatChar();
                    v = parseExpression();
                    if (c == ')') eatChar();
                } else { // numbers
                    int startIndex = this.pos;
                    while ((c >= '0' && c <= '9') || c == '.') eatChar();
                    if (pos == startIndex) throw new RuntimeException("Unexpected: " + (char)c);
                    v = Double.parseDouble(str.substring(startIndex, pos));
                }

                eatSpace();
                if (c == '^') { // exponentiation
                    eatChar();
                    v = Math.pow(v, parseFactor());
                }
                if (negate) v = -v; // unary minus is applied after exponentiation; e.g. -3^2=-9
                return v;
            }
        }
        return new Parser().parse();
    }
    
}
