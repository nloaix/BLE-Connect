package com.jht.bleconnect.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指令结构域注解
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CField {
    /**
     * 起点索引
     *
     * @return
     */
    int start();

    /**
     * 数据长度
     *
     * @return
     */
    int length() default -1;

    /**
     * 数据格式
     *
     * @return
     */
    Format format() default Format.NORMAL;

    /**
     * 不参与字节数组的组合输出
     *
     * @return
     */
    boolean unbonded() default false;

    /**
     * 数据格式
     */
    public enum Format {
        NORMAL, // 默认（根据变量类型指定）

        BYTE(1),
        BYTE_ARRAY,
        INT,
        INT_LH, // 低位前，高位后
        SHORT,
        SHORT_LH, // 低位前，高位后
        TEXT,
        TEXT_UTF8,
        TEXT_ASCII,
        TEXT_HEX;

        private int length;

        private Format() {
        }

        private Format(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }
    }
}
