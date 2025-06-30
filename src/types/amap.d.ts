// types/amap.d.ts
declare global {
  namespace AMap {
    // 基础类
    class Map {
      constructor(container: HTMLElement, options?: MapOptions);
      destroy(): void;
      add(overlay: Overlay): void;
      setCenter(lnglat: LngLat | [number, number]): void;
      setZoom(zoom: number): void;
    }
    class Marker {
      constructor(options?: MarkerOptions);
      on(event: string, callback: (event: MapsEvent) => void): void;
      getPosition(): LngLat;
      setPosition(lnglat: LngLat | [number, number]): void;
    }
    class InfoWindow {
      constructor(options?: InfoWindowOptions);
      open(map: Map, position: LngLat | [number, number]): void;
    }
    class Polyline {
      constructor(options?: PolylineOptions);
    }
    class LngLat {
      constructor(lng: number, lat: number);
    }
    class Pixel {
      constructor(x: number, y: number);
    }

    // 配置接口
    interface MapOptions {
      zoom?: number;
      center?: LngLat | [number, number];
      resizeEnable?: boolean;
    }
    interface MarkerOptions {
      position?: LngLat | [number, number];
      icon?: string;
      offset?: Pixel;
    }
    interface InfoWindowOptions {
      isCustom?: boolean;
      content?: string;
      offset?: Pixel;
    }
    interface PolylineOptions {
      path?: (LngLat | [number, number])[];
      strokeColor?: string;
      strokeWeight?: number;
      strokeStyle?: string;
    }
    interface MapsEvent {
      target: Marker;
    }

    // 全局暴露
    const Map: typeof Map;
    const Marker: typeof Marker;
    const InfoWindow: typeof InfoWindow;
    const Polyline: typeof Polyline;
    const LngLat: typeof LngLat;
    const Pixel: typeof Pixel;
  }

  // 全局变量
  const AMap: typeof AMap;
}

export {};