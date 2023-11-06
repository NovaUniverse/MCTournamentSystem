type EventHandler = (...args: any[]) => void;

export default class EventEmitter {
	private events: Record<string, EventHandler[]> = {};

	on(eventName: string, callback: EventHandler) {
		if (this.events[eventName] == null) {
			this.events[eventName] = [];
		}
		this.events[eventName].push(callback);
	}

	emit(eventName: string, ...args: any[]) {
		const eventHandlers = this.events[eventName];
		if (eventHandlers) {
			for (const handler of eventHandlers) {
				try {
					handler(...args);
				} catch (err) {
					console.error(" ----- Error occured in event handler ------");
					console.log(handler);
					console.error(err)
					console.error(" -------------------------------------------");
				}
			}
		}
	}

	off(eventName: string, callback: EventHandler) {
		const eventHandlers = this.events[eventName];
		if (eventHandlers) {
			const index = eventHandlers.indexOf(callback);
			if (index !== -1) {
				eventHandlers.splice(index, 1);
			}
		}
	}
}