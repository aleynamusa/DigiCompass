import React, { useState } from "react";
import dayjs from "dayjs";
import { Calendar } from "@mantine/dates";

const ShowCalendarForBirthDate = ({ value, onChange, error }) => {
    const [calendarOpen, setCalendarOpen] = useState(false);

    const handleSelect = (date) => {
        const formattedDate = dayjs(date).format("YYYY-MM-DD");
        onChange({ target: { name: "birthDate", value: formattedDate } });
        setCalendarOpen(false);
    };

    return (
        <div className="text-left relative z-0 w-full mb-5 group">
            <input
                type="text"
                name="birthDate"
                id="birthDate"
                readOnly
                onClick={() => setCalendarOpen(!calendarOpen)}
                value={value || ""}
                placeholder=" "
                className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none
          dark:text-white dark:border-slate-400 dark:focus:border-amber-50 focus:outline-none focus:ring-0 focus:border-amber-50 peer cursor-pointer"
                required
            />
            <label
                htmlFor="birthDate"
                className="peer-focus:font-medium absolute text-sm text-gray-800 dark:text-gray-400 duration-300 transform
          -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto
          peer-focus:text-amber-50 peer-focus:dark:text-amber-50 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
          peer-focus:scale-75 peer-focus:-translate-y-6"
            >
                Date of Birth:
            </label>

            {error && <p style={{ color: "red" }}>{error}</p>}

            {calendarOpen && (
                <div
                    className="absolute bottom-full mb-2 bg-white dark:bg-gray-800 shadow-lg rounded-xl p-3 z-[9999]"
                    style={{ transform: "translateY(-8px)" }}
                >
                    <Calendar
                        size="sm"
                        style={{ color: "black" }}
                        getDayProps={(date) => ({
                            selected: value && dayjs(date).isSame(dayjs(value), "date"),
                            onClick: () => handleSelect(date),
                        })}
                    />
                </div>
            )}
        </div>
    );
};

export default ShowCalendarForBirthDate;
