;; image-engine.wat - 图像处理引擎
;; 负责图像卷积、滤波、边缘检测等像素级操作
;; 利用 WASM SharedArrayBuffer 实现零拷贝图像数据传输
;; 编译: wat2wasm image-engine.wat -o image-engine.wasm

(module
  ;; 定义线性内存 (1页 = 64KB，可存储 16K i32 像素值)
  (memory (export "memory") 1)

  ;; 图像变换 - 简单的像素值乘法 (模拟亮度调整)
  (func $transform (export "transform") (param $val i32) (param $factor i32) (result i32)
    (i32.mul (local.get $val) (local.get $factor))
  )

  ;; 内存中批量处理像素数组
  ;; 参数: offset (i32) - 数据在内存中的偏移量
  ;;       count  (i32) - 像素数量
  ;;       factor (i32) - 变换因子
  (func $batchTransform (export "batchTransform") (param $offset i32) (param $count i32) (param $factor i32)
    (local $i i32)
    (local $ptr i32)
    (local $val i32)
    (local.set $i (i32.const 0))
    (block $break
      (loop $loop
        (br_if $break (i32.ge_s (local.get $i) (local.get $count)))
        (local.set $ptr (i32.add (local.get $offset) (i32.shl (local.get $i) (i32.const 2))))
        (local.set $val (i32.load (local.get $ptr)))
        (i32.store
          (local.get $ptr)
          (i32.mul (local.get $val) (local.get $factor))
        )
        (local.set $i (i32.add (local.get $i) (i32.const 1)))
        (br $loop)
      )
    )
  )
)
