import { Inject, Injectable } from '@angular/core';
import { Color } from '../models/Color';
import { DoublyLinkedList } from '../models/DoublyLinkedList';


@Injectable({
  providedIn: 'root'
})
export class ColorProviderService {

  private colorList: DoublyLinkedList<Color> = new DoublyLinkedList<Color>();

  constructor(@Inject('PROFILE_COLORS') private PROFILE_COLORS: any) {
    
    PROFILE_COLORS.forEach((hex: string) => this.colorList.appendLast(new Color(hex)));
  }

  //Brings the color at the head of the list and  remove that color from the list.
  getColor(): Color {
    const node: any = this.colorList.head;
    this.colorList.delete(node);
    return node.data;
  }
  // appends the color at the last.
  addColor = (color: Color) => this.colorList.appendLast(color);
}
