import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';


@Component({
  selector: 'app-btn',
  templateUrl: './btn.component.html',
  styleUrls: ['./btn.component.css']
})
export class BtnComponent implements OnInit {
  
  @Input() label: string = "";
  @Input() isCancel = false;
  @Input() icon="";
  @Input() disabled = false;
  @Output() clicked = new EventEmitter();

  constructor() { }
  sendEvent(){
    this.clicked.emit();
  }

  ngOnInit(): void {
  }

}
