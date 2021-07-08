import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.css']
})
export class DropdownComponent implements OnInit {

  @Input() dropdownList: string [] = [];
  @Input() label: string = "";
  @Input() defaultValue: string = "";
  @Input() disabled = false;
  @Output() change = new EventEmitter();
  constructor() {
    
   }

  ngOnInit(): void {
  }
  sendValue(event: any){
    this.change.emit(event.value);
  }

}
