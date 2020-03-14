/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.proxy;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 *
 * 字节码生成的代理类
 * public class proxy0 implements ClassGenerator.DC, EchoService, DemoService {
 *     // 方法数组
 *     public static Method[] methods;
 *     private InvocationHandler handler;
 *
 *     public proxy0(InvocationHandler invocationHandler) {
 *         this.handler = invocationHandler;
 *     }
 *
 *     public proxy0() {
 *     }
 *
 *     public String sayHello(String string) {
 *         // 将参数存储到 Object 数组中
 *         Object[] arrobject = new Object[]{string};
 *         // 调用 InvocationHandler 实现类的 invoke 方法得到调用结果
 *         Object object = this.handler.invoke(this, methods[0], arrobject);
 *         // 返回调用结果
 *         return (String)object;
 *     }
 *
 *     public Object $echo(Object object) {
 *         Object[] arrobject = new Object[]{object};
 *         Object object2 = this.handler.invoke(this, methods[1], arrobject);
 *         return object2;
 *     }
 */

/**
 * InvokerHandler
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private final Invoker<?> invoker;

    public InvokerInvocationHandler(Invoker<?> handler) {
        this.invoker = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 拦截定义在 Object 类中的方法（未被子类重写），比如 wait/notify
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        // 如果 toString、hashCode 和 equals 等方法被子类重写了，这里也直接调用
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        // 将 method 和 args 封装到 RpcInvocation 中，并执行后续的调用
        return invoker.invoke(new RpcInvocation(method, args)).recreate();
    }

}
