package com.alfredvc;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class used to parse strings into Function objects.
 *
 *
 * Given strings must be in one of the forms:
 *      (returnType=parameterType:param1,param2)-> EXPRESSION
 *      (returnType=parameterType:param1,param2)-> return EXPRESSION;
 *
 * For example:
 *      (double=Double:x,y,z,f)->(x + y + z + f)
 *      (double=java.util.List:l)->double tot = 1; for(java.util.Iterator iterator = ((java.util.List) l).iterator(); iterator.hasNext(); ){ Object o = iterator.next();tot*=((Double)o).doubleValue();} return tot;
 *
 */
public class FunctionParser
{
    private FunctionParser(){
        //Intentionally empty.
    }

    public static final String DEFAULT_RETURN_TYPE = "Object";
    public static final String BEHIND = "(?<=\\(|\\)|\\.|\\*|\\+|\\-|\\/|\\s|^|\\%|\\?|;|\\{|\\}|,)";
    public static final String AHEAD = "(?=\\(|\\)|\\.|\\*|\\+|\\-|\\/|\\s|$|\\%|;|\\?|\\{|\\}|,)";
    /**
     * Map of the supported primitive types
     */
    public static final Set<String> supportedPrimitives;
    static {
        Set<String> set = new HashSet<>();
        set.add("double");
        set.add("float");
        set.add("int");
        set.add("long");
        set.add("boolean");
        set.add("short");
        supportedPrimitives = Collections.unmodifiableSet(set);
    }
    private static final Map<String, String> primitiveToClass;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("double", "Double");
        map.put("float", "Float");
        map.put("int", "Integer");
        map.put("long", "Long");
        map.put("boolean", "Boolean");
        map.put("short", "Short");
        primitiveToClass = Collections.unmodifiableMap(map);
    }
    private static final Map<String,String> classToPrimitive;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("Double", "double");
        map.put("Float", "float");
        map.put("Integer", "int");
        map.put("Long", "long");
        map.put("Boolean", "boolean");
        map.put("Short", "short");
        classToPrimitive = Collections.unmodifiableMap(map);
    }

    /**
     * Parses strings into functions, the functions that can be parsed are subject to the same limitations
     * as in the Javaassist library. For more information on these visit http://jboss-javassist.github.io/.
     *
     * The returned class will override the relevant evaluate method of the Function interface with a
     * method that is equivalent to the given functionString. All other evaluate methods return an
     * Unsupported operation exception.
     *
     * @param functionString the string to be parsed
     * @return a class implementing the Function interface
     * @throws IllegalArgumentException are thrown with nested Javaassist exceptions, most of these exeptions are due to errors in the functionString.
     */
    public static Function fromString(String functionString)  {
        try {
            //TODO: validate functionString.
            String argsName = "o"+System.currentTimeMillis();
            LinkedHashSet<String> variables = new LinkedHashSet<>();
            String paramsString = functionString.split("\\(")[1].split("\\)")[0];
            String[] tempSplit = paramsString.split("=");
            String returnType = tempSplit.length > 1 ? tempSplit[0] : DEFAULT_RETURN_TYPE;
            paramsString = tempSplit[tempSplit.length - 1];
            List<String> types = new ArrayList<>();
            String methodBody = getMethodBody(functionString);
            String[] typesAndVariables = paramsString.split("\\|");
            int varNr = 0;
            for (String typeAndVariables : typesAndVariables) {
                String type = typeAndVariables.split("\\:")[0].trim();
                types.add(type);
                String[] params = typeAndVariables.split("\\:")[typeAndVariables.split("\\:").length - 1].split(",");
                //Order must be preserved
                for (String param : params) {
                    variables.add(param.trim());
                    methodBody = methodBody.replaceAll(BEHIND + param.trim() + AHEAD, getReplaceForVariableAndType(param.trim(),type, varNr, argsName));
                    varNr++;
                }
            }


            ClassPool pool = ClassPool.getDefault();
            CtClass evalClass = pool.makeClass("Eval" + System.currentTimeMillis());
            evalClass.addField(new CtField(pool.get("java.util.LinkedHashSet"), "variableSet", evalClass));
            evalClass.setInterfaces(
                    new CtClass[]{pool.makeClass("com.alfredvc.Function")});


            String methodString = getMethodString(argsName, returnType, methodBody);


            evalClass.addMethod(
                    CtNewMethod.make(methodString, evalClass));
            evalClass.addMethod(
                    CtNewMethod.make("public java.util.LinkedHashSet getVariableSet(){return this.variableSet;}", evalClass)
            );

            evalClass.addMethod(
                    CtNewMethod.make("public void setVariableSet(java.util.LinkedHashSet s){this.variableSet = s;}", evalClass)
            );

            Class clazz = evalClass.toClass();
            Function obj  = (Function) clazz.newInstance();
            clazz.getMethod("setVariableSet", java.util.LinkedHashSet.class).invoke(obj, variables);
            return obj;
        } catch (CannotCompileException e) {
            throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String getMethodString(String argsName, String returnType, String methodBody) {
        String methodString;

        if (methodBody.split(BEHIND +"return"+ AHEAD).length > 1) {
            methodString = "public "+ getMethodNameAndReturnType(returnType)+"(Object[] "+argsName+"){"+methodBody+"}";
        } else {
            methodString = "public "+ getMethodNameAndReturnType(returnType)+"(Object[] "+argsName+"){return (("+returnType+")(" + methodBody+"));}";
        }
        return methodString;
    }

    private static String getMethodBody(String functionString) {
        return functionString.split("->", 2)[1];
    }

    private static String getReplaceForVariableAndType(String var, String type, int varNr, String argsName) {
        String toReplace;
        String returnType = type;
        if (classToPrimitive.containsKey(type)) {
            returnType = classToPrimitive.get(type);
            toReplace = "((("+type+") "+argsName+"["+varNr+"])."+returnType+"Value())";
        } else {
            toReplace = "(("+type+") "+ argsName+"["+varNr+"])";
        }
        return toReplace;
    }



    private static String getMethodNameAndReturnType(String returnType){
        if (supportedPrimitives.contains(returnType)) {
            return returnType+" "+"evaluateTo"+primitiveToClass.get(returnType);
        } else {
            return "Object evaluateInternal";
        }
    }
}
