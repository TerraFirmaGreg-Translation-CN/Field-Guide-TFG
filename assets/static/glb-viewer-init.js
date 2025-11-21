/**
 * GLB 查看器初始化脚本
 * 自动查找并初始化页面中的 GLB 查看器包装器
 */

(function() {
    'use strict';
    
    let viewerCounter = 0;
    
    /**
     * 初始化所有 GLB 查看器包装器
     */
    async function initGLBViewers() {
        // 等待 DOM 加载完成
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', initGLBViewers);
            return;
        }
        
        // 等待 GLBViewer 类加载完成
        await waitForGLBViewer();
        
        // 查找所有查看器包装器
        const wrappers = document.querySelectorAll('.glb-viewer-wrapper');
        
        for (const wrapper of wrappers) {
            try {
                await createViewerFromWrapper(wrapper);
            } catch (error) {
                console.error('Failed to create viewer from wrapper:', error);
                wrapper.innerHTML = `
                    <div class="alert alert-danger">
                        无法初始化 GLB 查看器: ${error.message}
                    </div>
                `;
            }
        }
    }
    
    /**
     * 等待 GLBViewer 类加载完成
     */
    function waitForGLBViewer() {
        return new Promise((resolve) => {
            const checkInterval = setInterval(() => {
                if (typeof window.GLBViewer !== 'undefined') {
                    clearInterval(checkInterval);
                    resolve();
                }
            }, 100);
            
            // 超时保护
            setTimeout(() => {
                clearInterval(checkInterval);
                console.error('GLBViewer class not found after timeout');
                resolve();
            }, 10000);
        });
    }
    
    /**
     * 从包装器创建查看器实例
     */
    async function createViewerFromWrapper(wrapper) {
        // 读取属性
        const viewerId = wrapper.getAttribute('data-viewer-id') || `viewer-${++viewerCounter}`;
        const modelUrl = wrapper.getAttribute('data-model-url');
        const scaleAttr = wrapper.getAttribute('data-scale') || '[1, 1, 1]';
        
        // 解析缩放参数
        let scale;
        try {
            scale = JSON.parse(scaleAttr);
        } catch (e) {
            console.warn('Invalid scale attribute, using default [1, 1, 1]');
            scale = [1, 1, 1];
        }
        
        // 创建容器 HTML
        const containerHtml = `
            <div class="glb-viewer-container" id="glb-viewer-${viewerId}">
                <!-- 拖拽覆盖层 -->
                <div id="drop-overlay-${viewerId}" style="
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(0, 123, 255, 0.1);
                    border: 3px dashed #007bff;
                    border-radius: 8px;
                    display: none;
                    align-items: center;
                    justify-content: center;
                    z-index: 1000;
                    pointer-events: none;
                ">
                    <div style="text-align: center; color: #007bff;">
                        <i class="bi bi-cloud-upload" style="font-size: 48px;"></i>
                        <h4>拖拽 GLB 文件到此处</h4>
                        <p>支持 .glb 和 .gltf 格式</p>
                    </div>
                </div>
            </div>
        `;
        
        wrapper.innerHTML = containerHtml;
        
        // 创建查看器实例
        const viewer = new window.GLBViewer(`glb-viewer-${viewerId}`, {
            width: 'auto',
            height: 'auto',
            backgroundColor: 0xe6f3ff,
            enableControls: true,
            enableGrid: true,
            enableAxes: false, // 在文档中隐藏坐标轴
            enableShadows: true,
            autoRotate: false,
            minDistance: 3,
            maxDistance: 30
        });
        
        // 如果有默认模型 URL，自动加载
        if (modelUrl) {
            try {
                await viewer.loadGLB(modelUrl, {
                    position: [0, 0, 0],
                    scale: scale
                });
                console.log(`Default model loaded for ${viewerId}: ${modelUrl}`);
            } catch (error) {
                console.error(`Failed to load default model for ${viewerId}:`, error);
                // 显示错误信息但不阻止查看器使用
                viewer.showError(`加载默认模型失败: ${error.message}`);
            }
        }
        
        // 将实例存储到全局对象，方便调试和访问
        window.glbViewers = window.glbViewers || {};
        window.glbViewers[viewerId] = viewer;
        
        console.log(`GLB Viewer ${viewerId} initialized successfully`);
        
        // 添加数据属性以便后续访问
        wrapper.dataset.viewerId = viewerId;
    }
    
    /**
     * 创建新的查看器（函数式 API）
     */
    window.createGLBViewer = function(containerId, options = {}) {
        return new Promise((resolve, reject) => {
            waitForGLBViewer().then(() => {
                try {
                    const viewer = new window.GLBViewer(containerId, {
                        backgroundColor: 0xe6f3ff,
                        enableControls: true,
                        enableGrid: true,
                        enableAxes: false,
                        enableShadows: true,
                        autoRotate: false,
                        minDistance: 3,
                        maxDistance: 30,
                        ...options
                    });
                    resolve(viewer);
                } catch (error) {
                    reject(error);
                }
            }).catch(reject);
        });
    };
    
    /**
     * 获取查看器实例
     */
    window.getGLBViewer = function(viewerId) {
        return window.glbViewers?.[viewerId] || null;
    };
    
    /**
     * 重新初始化所有查看器
     */
    window.reinitGLBViewers = initGLBViewers;
    
    // 自动初始化
    initGLBViewers();
    
})();