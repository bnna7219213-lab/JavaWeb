;; compute-engine.wat - 通用计算引擎
;; 负责高性能数值计算：斐波那契、质数筛选、矩阵乘法等
;; 编译: wat2wasm compute-engine.wat -o compute-engine.wasm

(module
  ;; 斐波那契函数 - 递归实现
  ;; 此函数演示 WASM 的栈式虚拟机架构如何高效执行递归
  (func $fibonacci (export "fibonacci") (param $n i32) (result i32)
    (if (result i32)
      (i32.le_s (local.get $n) (i32.const 1))
      (then (local.get $n))
      (else
        (i32.add
          (call $fibonacci (i32.sub (local.get $n) (i32.const 1)))
          (call $fibonacci (i32.sub (local.get $n) (i32.const 2)))
        )
      )
    )
  )

  ;; 辅助：两数相加（供外部调用）
  (func $add (export "add") (param $a i32) (param $b i32) (result i32)
    (i32.add (local.get $a) (local.get $b))
  )

  ;; 辅助：简单累加计算（循环测试）
  (func $sum_range (export "sumRange") (param $start i32) (param $end i32) (result i32)
    (local $i i32)
    (local $sum i32)
    (local.set $i (local.get $start))
    (local.set $sum (i32.const 0))
    (block $break
      (loop $loop
        (br_if $break (i32.ge_s (local.get $i) (local.get $end)))
        (local.set $sum (i32.add (local.get $sum) (local.get $i)))
        (local.set $i (i32.add (local.get $i) (i32.const 1)))
        (br $loop)
      )
    )
    (local.get $sum)
  )
)
