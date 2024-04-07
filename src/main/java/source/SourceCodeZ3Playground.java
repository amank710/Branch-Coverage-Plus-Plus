//<<<<<<< HEAD
//package source;
//
//public class SourceCodeZ3Playground {
////    public static Boolean booleanFlags(boolean flag, int ff) {
////
////        // No variable tracking
////        // output is true and a is false but line 12 is satisfiable and line 14 is not
////        // because we never tell z3 what the values are that we are parsing it just
////        // takes boolean on face value
////
////        boolean output = flag;
////        boolean a = false;
////        output = a;
//////        output = a;
////        int abc = 5;
////
////        if (ff > 2) { // Satisfiable
////            System.out.println("print");
////
////            if (flag) { // Satisfiable
//////                output = true;
////                if (ff > 5) {
//////                    output = false;
////                    if (ff > 7) {
////                        output = false;
////                    } else {
////                        output = false;
////
////                    }
////                } else {
////                    if (ff < 4) {
////                        if (ff == 3) { // always true
////                            output = false;
////
////                            return output;
////                        }
////                        output = false;
////                    } else if (ff == 4) {
////                        output = true;
////                        if (ff < 4) {
////                            if (ff == 3) { // always true
////                                output = false;
////                                return output;
////
////                            } else {
////                                output = false; //ignore
////                            }
////                            output = false;
////                        } else {
////                            output = false;
////                        }
////                    } else {
////                        output = false;
////                    }
////                    output = true;
////                }
////
////                //[19,17, 23][21,17, 23]
////
////
////            }
////        }
////    }
////}
