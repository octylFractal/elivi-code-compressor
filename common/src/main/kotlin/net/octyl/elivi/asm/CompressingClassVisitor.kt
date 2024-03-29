/*
 * This file is part of elivi, licensed under the MIT License (MIT).
 *
 * Copyright (c) Octavia Togami <https://octyl.net>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.octyl.elivi.asm

import net.octyl.elivi.CompressOption
import net.octyl.elivi.CompressOption.REMOVE_SIGNATURE
import net.octyl.elivi.CompressOption.REMOVE_SOURCEFILE
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class CompressingClassVisitor(
    private val flags: Set<CompressOption>,
    delegate: ClassVisitor? = null
) : ClassVisitor(Opcodes.ASM7, delegate) {

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        val realSignature = signature.takeUnless { REMOVE_SIGNATURE in flags }
        super.visit(version, access, name, realSignature, superName, interfaces)
    }

    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor {
        val realSignature = signature.takeUnless { REMOVE_SIGNATURE in flags }
        return super.visitField(access, name, descriptor, realSignature, value)
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val realSignature = signature.takeUnless { REMOVE_SIGNATURE in flags }
        val visitor = super.visitMethod(access, name, descriptor, realSignature, exceptions)
        return CompressingMethodVisitor(flags, visitor)
    }

    override fun visitSource(source: String?, debug: String?) {
        if (REMOVE_SOURCEFILE !in flags) {
            super.visitSource(source, debug)
        }
    }
}