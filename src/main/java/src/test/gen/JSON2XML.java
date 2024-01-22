package src.test.gen;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

class XMLEmitter extends JSONBaseListener{
    public ParseTreeProperty<String> xml = new ParseTreeProperty<String>();
    String getXML(ParseTree ctx){
        return xml.get(ctx);
    }

    void setXML(ParseTree ctx,String s){
        xml.put(ctx, s);
    }

    public void exitAtom(JSONParser.AtomContext ctx) {
        setXML(ctx, ctx.getText());
    }

    public void exitArrayValue(JSONParser.ArrayValueContext ctx) {
        setXML(ctx,getXML(ctx.array()));
    }

    public void exitString(JSONParser.StringContext ctx) {
        setXML(ctx,ctx.getText().replaceAll("\"", ""));
    }

    public void exitObjectValue(JSONParser.ObjectValueContext ctx) {
        setXML(ctx,getXML(ctx.object()));
    }

    public void exitPair(JSONParser.PairContext ctx) {
        String tag = ctx.STRING().getText().replace("\"", "");
        JSONParser.ValueContext vctx = ctx.value();
        String x = String.format("<%s>%s<%s>\n",tag,getXML(vctx),tag);
        setXML(ctx,x);
    }

    public void exitAnObject(JSONParser.AnObjectContext ctx) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        for(JSONParser.PairContext pctx : ctx.pair()){
            buf.append(getXML(pctx));
        }
        setXML(ctx,buf.toString());
    }

    public void exitEmptyObject(JSONParser.EmptyObjectContext ctx) {
        setXML(ctx,"");
    }

    public void exitArrayOfValues(JSONParser.ArrayOfValuesContext ctx) {
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        for(JSONParser.ValueContext vctx : ctx.value()){
            buf.append("<element>")
                    .append(getXML(vctx))
                    .append("<element>")
                    .append("\n");
        }
        setXML(ctx,buf.toString());
    }

    public void exitEmptyArray(JSONParser.EmptyArrayContext ctx) {
        setXML(ctx,"");
    }

    public void exitJson(JSONParser.JsonContext ctx) {
        setXML(ctx,getXML(ctx.getChild(0)));
    }
}

public class JSON2XML {
    public static void main(String[] args) throws IOException {
        String path = "/Users/mac/Library/Mobile Documents/com~apple~CloudDocs/jsoninxml/src/main/java/src/test/gen/test.json";
        CharStream inputStream = CharStreams.fromFileName(path);
        JSONLexer lexer = new JSONLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokenStream);
        ParseTreeWalker walker = new ParseTreeWalker();
        XMLEmitter xml = new XMLEmitter();
        ParseTree json = parser.json();
        walker.walk(xml,json);
        System.out.println(xml.xml.get(json));
    }
}

