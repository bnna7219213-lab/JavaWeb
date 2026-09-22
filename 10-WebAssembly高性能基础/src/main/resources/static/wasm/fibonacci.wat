;; fibonacci.wat - 斐波那契计算模块 (WebAssembly Text Format)
;; 使用 wat2wasm 工具编译为 .wasm 二进制文件
;; wat2wasm fibonacci.wat -o fibonacci.wasm

(module
  ;; 斐波那契函数 - 递归实现
  ;; 参数: n (i32)
  ;; 返回: fib(n) (i32)
  (func $fibonacci (export "fibonacci") (param $n i32) (result i32)
    ;; if n <= 1: return n
    (if (result i32)
      (i32.le_s (local.get $n) (i32.const 1))
      (then (local.get $n))
      ;; else: return fib(n-1) + fib(n-2)
      (else
        (i32.add
          (call $fibonacci (i32.sub (local.get $n) (i32.const 1)))
          (call $fibonacci (i32.sub (local.get $n) (i32.const 2)))
        )
      )
    )
  )
)
