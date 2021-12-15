package com.jht.bleconnect.common;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 指令注解操作类
 */

public class CFieldUtil {

    private static final String FIELD_NAME_MESSAGEID = "messageId";
    private static final String FIELD_NAME_ACK_MESSAGEID = "ackMessageId";
    private static final String FIELD_NAME_ACK = "ack";
    private static final String FIELD_NAME_DATA = "data";

    /**
     * 转换为字节数据
     *
     * @param obj
     */
    @Deprecated
    public static byte[] getBytesOld(Object obj) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        // 父类
        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            Field[] superFields = superClass.getDeclaredFields();
            fields.addAll(Arrays.asList(superFields));
        }
        // 本类
        Field[] thisFields = clazz.getDeclaredFields();
        fields.addAll(Arrays.asList(thisFields));

        byte[] result = new byte[0];
        for (Field field : fields) {
            if (field.isAnnotationPresent(CField.class)) {
                CField cField = (CField) field.getAnnotation(CField.class);
                if (!cField.unbonded()) {
                    boolean accessible = field.isAccessible();
                    try {
                        // 取值
                        field.setAccessible(true);
                        System.out.println(field.getName() + " = " + field.get(obj) + " " + field.getType());

                        switch (cField.format()) {
                            case BYTE:
                                byte vByte = field.getByte(obj);
                                result = CmdUtil.mergeByte(result, new byte[]{vByte});
                                break;

                            case INT:
                                int vInt = field.getInt(obj);
                                result = CmdUtil.mergeByte(result, CmdUtil.int2bytes(vInt));
                                break;

                            case INT_LH:
                                int vIntLH = field.getInt(obj);
                                result = CmdUtil.mergeByte(result, CmdUtil.int2bytesLH(vIntLH));
                                break;

                            case SHORT:
                                short vShortInt = field.getShort(obj);
                                result = CmdUtil.mergeByte(result, CmdUtil.short2bytes(vShortInt));
                                break;

                            case SHORT_LH:
                                short vShortIntLH = field.getShort(obj);
                                result = CmdUtil.mergeByte(result, CmdUtil.short2bytesLH(vShortIntLH));
                                break;

                            case TEXT:
                                result = CmdUtil.mergeByte(result, ((String) field.get(obj)).getBytes());
                                break;

                            case TEXT_ASCII:
                                result = CmdUtil.mergeByte(result, ((String) field.get(obj)).getBytes());
                                break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO: 输出解码错误信息（要求包含字段名、类型等信息）
                    } finally {
                        field.setAccessible(accessible);
                    }
                }
            }
        }

        byte[] crcBytes = Arrays.copyOfRange(result, 8, 10);
        if (Arrays.equals(crcBytes, new byte[]{0, 0})) {
            byte[] contentBytes = Arrays.copyOfRange(result, 10, result.length);
            crcBytes = CmdUtil.checkSum(contentBytes);

            for (int i = 8; i < 10; i++) {
                result[i] = crcBytes[i - 8];
            }
        }

        //System.out.println("byte array -- = -- " + CmdUtil.bytes2HexString(result));
        return result;
    }

//	/**
//	 * 根据指令字节数据转换对象
//	 *
//	 * @param obj
//	 * @param src
//	 */
//	@Deprecated
//	public static void parse(Object obj, byte[] src) {
//
//		List<Field> fields = new ArrayList<>();
//		Class<?> clazz = obj.getClass();
//		// 父类
//		Class superClass = clazz.getSuperclass();
//		if (superClass != null) {
//			Field[] superFields = superClass.getDeclaredFields();
//			fields.addAll(Arrays.asList(superFields));
//		}
//		// 本类
//		Field[] thisFields = clazz.getDeclaredFields();
//		fields.addAll(Arrays.asList(thisFields));
//
//		boolean isAck = false; // 返回错误报告
//
//		for (Field field : fields) {
//			if (field.isAnnotationPresent(CField.class)) {
//				CField annotation = (CField) field.getAnnotation(CField.class);
//				boolean accessible = field.isAccessible();
//				try {
//					// 取值
//					field.setAccessible(true);
//					System.out.println(field.getName() + " = " + annotation.start() + " " + annotation.length());
//
//					byte[] temp;
//					if (annotation.length() <= 0) {
//						temp = CmdUtil.subBytes(src, annotation.start(), src.length - annotation.start());
//					} else {
//						temp = CmdUtil.subBytes(src, annotation.start(), annotation.length());
//					}
//					System.out.println("" + CmdUtil.bytes2HexString(temp));
//
//					if (field.getType() == String.class) {
//						if (annotation.format() == CField.Format.TEXT_ASCII) {
//							field.set(obj, CmdUtil.bytes2Ascii(temp));
//						} else {
//							field.set(obj, CmdUtil.bytes2HexString(temp));
//						}
//
//					} else if (field.getType() == int.class) {
//						field.setInt(obj, CmdUtil.byte2int(temp));
//
//					} else if (field.getType() == byte.class) {
//						// 非ack指令时，不为ack_messageId、ack赋值
//						if (!isAck && (field.getName().equals("ack_messageId") || field.getName().equals("ack"))) {
//							continue;
//						}
//						field.setByte(obj, temp[0]);
//						// messageId为0x00时，整条指令为ACK结果
//						if (field.getName().equals("messageId") && temp[0] == 0x00) {
//							isAck = true;
//						}
//
//					} else if (field.getType() == short.class) {
//						short s;
//						if (annotation.format() == CField.Format.SHORT_LH) {
//							s = CmdUtil.byte2shortLH(temp);
//						} else {
//							s = CmdUtil.byte2short(temp);
//						}
//						field.set(obj, s);
//
//					} else if (field.getType() == byte[].class) {
//						field.set(obj, temp);
//					}
//
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} finally {
//					field.setAccessible(accessible);
//				}
//			}
//		}
//	}

    public static void parse(byte[] src, Object obj) {
        parse(src, obj, true);
    }

    /**
     * @param obj
     * @param src
     * @param isNumberLH （所有）数字值采用低位在前、高位在后
     */
    public static void parse(byte[] src, Object obj, boolean isNumberLH) {

        List<Field> fields = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        // 父类
        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            Field[] superFields = superClass.getDeclaredFields();
            fields.addAll(Arrays.asList(superFields));
        }
        // 本类
        Field[] thisFields = clazz.getDeclaredFields();
        fields.addAll(Arrays.asList(thisFields));

//		boolean isAck = false; // ACK格式指令
//		int position = 0;

        for (Field field : fields) {
            if (field.isAnnotationPresent(CField.class)) {
                boolean accessible = field.isAccessible();
                try {
                    // 数据长度
                    int length = getBasicDataBytesLength(field.getType());
                    // 起始索引
                    int startPosition = 0;
                    // 数据类型
                    CField.Format format = CField.Format.NORMAL;
                    // 不参与数据拼接
                    boolean unbonded = false;
                    // 读取注解定义的值
                    CField annotation = (CField) field.getAnnotation(CField.class);
                    format = annotation.format();
                    unbonded = annotation.unbonded();
                    if (annotation.length() > 0) {
                        length = annotation.length();
                    }
                    if (annotation.start() >= 0) {
                        startPosition = annotation.start();
                    }

                    if (startPosition + length > src.length) {
                        continue;
                    }

//					// 非ack指令时，不为ack_messageId、ack赋值（此处的isAck会比ack_messageId优先得到赋值）
//					if (!isAck && (field.getName().equalsIgnoreCase(FIELD_NAME_ACK_MESSAGEID) || field.getName().equalsIgnoreCase(FIELD_NAME_ACK))) {
//						continue; // 下一个
//					}

                    // 取值
                    field.setAccessible(true);

                    byte[] temp;
                    if (length <= 0) {
                        //temp = CmdUtil.subBytes(src, startPosition, src.length - startPosition);
                        temp = Arrays.copyOfRange(src, startPosition, src.length);
                    } else {
                        //temp = CmdUtil.subBytes(src, startPosition, length);
                        temp = Arrays.copyOfRange(src, startPosition, startPosition + length);
                    }

                    if (field.getType() == String.class) {
                        if (format == CField.Format.TEXT_ASCII) {
                            field.set(obj, CmdUtil.bytes2Ascii(temp));
                        } else if (format == CField.Format.TEXT_HEX) {
                            field.set(obj, CmdUtil.bytes2HexString(temp));
                        } else if (format == CField.Format.TEXT_UTF8) {
                            field.set(obj, CmdUtil.bytes2Utf8String(temp));
                        } else {
                            field.set(obj, CmdUtil.bytes2HexString(temp));
                        }
                    } else if (field.getType() == int.class) {
                        int i;
                        if (format == CField.Format.INT_LH || isNumberLH) {
                            i = CmdUtil.bytes2intLH(temp, 0);
                        } else {
                            i = CmdUtil.bytes2int(temp, 0);
                        }
                        field.setInt(obj, i);
                    } else if (field.getType() == byte.class) {
                        field.setByte(obj, temp[0]);
                        // messageId为0x00时，整条指令为ACK结果
//						if (field.getName().equalsIgnoreCase(FIELD_NAME_MESSAGEID) && temp[0] == 0x00) {
//							isAck = true;
//						}
//
//						if (isAck && field.getName().equals("ack")) {
//							break; // 得到ack值后，停止解码
//						}
                    } else if (field.getType() == short.class) {
                        short s;
                        if (format == CField.Format.SHORT_LH || isNumberLH) {
                            s = CmdUtil.byte2shortLH(temp);
//							s = Short.reverseBytes(Array.getShort(temp, 0));
                        } else {
                            s = CmdUtil.byte2short(temp);
//							s = Array.getShort(temp, 0);
                        }
                        field.set(obj, s);
                    } else if (field.getType() == byte[].class) {
                        field.set(obj, temp);
                    }

//					if (!unbonded || isAck) {
//						position += length;
//					}

                    // TODO: 解码量已达messageSize，结束解码

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO: 输出解码错误信息（要求包含字段名、类型等信息）
                    e.printStackTrace();
                } finally {
                    field.setAccessible(accessible);
                }
            }
        }
    }

    /**
     * 基本数据的字节长度
     *
     * @param fieldType
     * @return
     */
    private static int getBasicDataBytesLength(Class<?> fieldType) {
        if (fieldType == byte.class || fieldType == Byte.class) {
            return 1;
        } else if (fieldType == short.class || fieldType == Short.class) {
            return 2;
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return 4;
        } else if (fieldType == long.class || fieldType == Long.class) {
            return 8;
        } else if (fieldType == float.class || fieldType == Float.class) {
            return 4;
        } else if (fieldType == double.class || fieldType == Double.class) {
            return 8;
        } else if (fieldType == char.class || fieldType == Character.class) {
            return 2;
        }
        return 0;
    }

    private static CField.Format getFieldFormat(Class<?> fieldType) {
        if (fieldType == byte.class || fieldType == Byte.class) {
            return CField.Format.BYTE;
        } else if (fieldType == short.class || fieldType == Short.class) {
            return CField.Format.SHORT;
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return CField.Format.INT;
        } else if (fieldType == String.class) {
            return CField.Format.TEXT;
        } else if (fieldType == byte[].class || fieldType == Byte[].class) {
            return CField.Format.BYTE_ARRAY;
        }
        return CField.Format.NORMAL;
    }

    public static byte[] getBytes(Object object) {
        return getBytes(object, true);
    }

    /**
     * 转换为字节数据
     *
     * @param obj
     * @param isNumberLH （所有）数字值采用低位在前、高位在后
     */
    public static byte[] getBytes(Object obj, boolean isNumberLH) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        // 父类
        Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            Field[] superFields = superClass.getDeclaredFields();
            fields.addAll(Arrays.asList(superFields));
        }
        // 本类
        Field[] thisFields = clazz.getDeclaredFields();
        fields.addAll(Arrays.asList(thisFields));

        boolean isAck = false;
        int resultLength = 0;
        for (Field field :
                fields) {
            if (field.isAnnotationPresent(CField.class)) {
                CField annotation = (CField) field.getAnnotation(CField.class);
                if (!annotation.unbonded()) {
                    int length = annotation.length();
                    if (length <= 0) {
                        length = getBasicDataBytesLength(field.getType());
                    }
                    resultLength += length;
                }
            }
        }

        try {
            Field fMessageId = superClass.getDeclaredField(FIELD_NAME_MESSAGEID);
            boolean accessible = fMessageId.isAccessible();
            fMessageId.setAccessible(true);
            if (fMessageId.getByte(obj) == 0) {
                resultLength += 2;
                isAck = true;
            }
            fMessageId.setAccessible(accessible);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        byte[] result = new byte[resultLength];
        for (Field field : fields) {
            if (field.isAnnotationPresent(CField.class)) {
                CField annotation = (CField) field.getAnnotation(CField.class);
                if (!annotation.unbonded()
                        || (isAck &&
                        !field.getName().equalsIgnoreCase(FIELD_NAME_DATA) &&
                        (field.getName().equalsIgnoreCase(FIELD_NAME_ACK)
                                || field.getName().equalsIgnoreCase(FIELD_NAME_ACK_MESSAGEID)))) {
                    boolean accessible = field.isAccessible();
                    try {
                        // 取值
                        field.setAccessible(true);
                        //System.out.println(field.getName() + " = " + field.get(obj) + " " + field.getType());

                        CField.Format fieldFormat = getFieldFormat(field.getType());
                        int start = annotation.start();


                        switch (fieldFormat) {
                            case BYTE:
                                byte vb = field.getByte(obj);
                                //result = CmdUtil.mergeByte(result, new byte[]{vb});
                                CmdUtil.fillBytes(result, start, new byte[]{vb});

                                if (field.getName().equalsIgnoreCase(FIELD_NAME_MESSAGEID) && vb == 0) {
                                    isAck = true;
                                }
                                break;

                            case SHORT:
                                short vs = field.getShort(obj);
                                if (annotation.format() == CField.Format.SHORT_LH || (isNumberLH && annotation.format() != CField.Format.SHORT)) {
                                    //result = CmdUtil.mergeByte(result, CmdUtil.short2bytesLH(vs));
                                    CmdUtil.fillBytes(result, start, CmdUtil.short2bytesLH(vs));
                                } else {
                                    //result = CmdUtil.mergeByte(result, CmdUtil.short2bytes(vs));
                                    CmdUtil.fillBytes(result, start, CmdUtil.short2bytes(vs));
                                }
                                break;

                            case INT:
                                int vi = field.getInt(obj);
                                if (annotation.format() == CField.Format.INT_LH || (isNumberLH && annotation.format() != CField.Format.INT)) {
                                    //result = CmdUtil.mergeByte(result, CmdUtil.int2bytesLH(vi));
                                    CmdUtil.fillBytes(result, start, CmdUtil.int2bytesLH(vi));
                                } else {
                                    //result = CmdUtil.mergeByte(result, CmdUtil.int2bytes(vi));
                                    CmdUtil.fillBytes(result, start, CmdUtil.int2bytes(vi));
                                }
                                break;

                            case TEXT:
                                String vstr = (String) field.get(obj);
                                byte[] vstrBytes = new byte[annotation.length()];
                                switch (annotation.format()) {
                                    case TEXT:
                                        CmdUtil.fillBytes(vstrBytes, 0, vstr.getBytes());
                                        break;
                                    case TEXT_ASCII:
                                        CmdUtil.fillBytes(vstrBytes, 0, vstr.getBytes());
                                        break;
                                    case TEXT_HEX:
                                        CmdUtil.fillBytes(vstrBytes, 0, CmdUtil.hexString2Bytes(vstr));
                                        break;
                                    case TEXT_UTF8:
                                        CmdUtil.fillBytes(vstrBytes, 0, CmdUtil.utf8String2Bytes(vstr));
                                        break;
                                }

                                CmdUtil.fillBytes(result, start, vstrBytes);
                                break;

                            case BYTE_ARRAY:
                                CmdUtil.fillBytes(result, start, (byte[]) field.get(obj));
                                break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO: 输出解码错误信息（要求包含字段名、类型等信息）
                    } finally {
                        field.setAccessible(accessible);
                    }
                }
            }
        }

        // size
        short size = (short) (result.length - 10);
        byte[] sizeBytes = CmdUtil.short2bytesLH(size);
//		result[6] = sizeBytes[0];
//		result[7] = sizeBytes[1];
        CmdUtil.fillBytes(result, 6, sizeBytes);

        // CRC
        if (result.length > 10) {
            byte[] crcBytes = Arrays.copyOfRange(result, 8, 10);
            if (Arrays.equals(crcBytes, new byte[]{0, 0})) {
                byte[] contentBytes = Arrays.copyOfRange(result, 10, result.length);
                crcBytes = CmdUtil.checkSum(contentBytes);
                CmdUtil.fillBytes(result, 8, crcBytes);
            }
        }

        //System.out.println("byte array -- = -- " + CmdUtil.bytes2HexString(result));
        return result;
    }


}

